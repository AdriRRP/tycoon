package org.tycoon.utils

import com.amazonaws.auth.{AWSStaticCredentialsProvider, BasicAWSCredentials}
import com.amazonaws.client.builder.AwsClientBuilder
import com.amazonaws.regions.Regions
import com.amazonaws.services.s3.model.{Bucket, ListVersionsRequest, PutObjectResult, VersionListing}
import com.amazonaws.services.s3.{AmazonS3, AmazonS3ClientBuilder}
import com.typesafe.scalalogging.Logger

import java.io.{File, FileOutputStream, InputStream, OutputStream}
import scala.jdk.CollectionConverters._
import scala.util.control.Breaks.{break, breakable}
import scala.util.{Failure, Success, Try}

/**
 * Provides encapsulated way to perform basic s3 operations
 *
 * @param accessKey S3 access key
 * @param secretKey S3 secret key
 * @param endpoint  S3 custom endpoint
 */
class S3Utils(
               accessKey: String,
               secretKey: String,
               endpoint: String,
             ) {

  // Class logger
  val logger: Logger = Logger(getClass.getName)

  // Set credentials
  val s3Credential = new BasicAWSCredentials(accessKey, secretKey)

  // Client to manage S3
  val s3Client: AmazonS3 = AmazonS3ClientBuilder
    .standard()
    .withEndpointConfiguration(
      new AwsClientBuilder.EndpointConfiguration(
        endpoint,
        Regions.DEFAULT_REGION.getName)
    )
    .withPathStyleAccessEnabled(true)
    .withCredentials(new AWSStaticCredentialsProvider(s3Credential))
    .build()

  /**
   * Create a new bucket or retrieve existing one with provided name
   * If bucket can't be retrieved returns None
   *
   * @param bucketName target bucket's name
   * @return optional target bucket object
   */
  def createBucket(bucketName: String): Option[Bucket] = {
    if (s3Client.doesBucketExistV2(bucketName)) {
      logger.warn(s"Bucket $bucketName already exists.")
      getBucket(bucketName);
    } else {
      Try(s3Client.createBucket(bucketName)) match {
        case Success(bucket) => Some(bucket)
        case Failure(exception) =>
          logger.error(exception.toString)
          None
      }
    }
  }

  /**
   * Retrieve existing bucket with provided name
   * If bucket can't be retrieved returns None
   *
   * @param bucketName target bucket's name
   * @return optional target bucket object
   */
  def getBucket(bucketName: String): Option[Bucket] =
    s3Client.listBuckets().asScala.toList.find(_.getName == bucketName)

  /**
   * Delete target bucket and all containing objects
   *
   * @param bucketName target bucket's name
   */
  def deleteBucket(bucketName: String): Unit = {
    var objectListing = s3Client.listObjects(bucketName)
    breakable {
      while (true) {
        objectListing.getObjectSummaries.iterator().asScala.toList
          .foreach(obj => s3Client.deleteObject(bucketName, obj.getKey))

        if (objectListing.isTruncated)
          objectListing = s3Client.listNextBatchOfObjects(objectListing)
        else
          break
      }
    }

    // Delete all object versions (required for versioned buckets).
    var versionList: VersionListing = s3Client.listVersions(new ListVersionsRequest().withBucketName(bucketName))
    breakable {
      while (true) {
        versionList.getVersionSummaries.iterator().asScala.toList
          .foreach(version => s3Client.deleteVersion(bucketName, version.getKey, version.getVersionId))

        if (versionList.isTruncated)
          versionList = s3Client.listNextBatchOfVersions(versionList);
        else
          break
      }
    }

    // After all objects and object versions are deleted, delete the bucket.
    s3Client.deleteBucket(bucketName);
  }

  /**
   * Upload resource file to target bucket
   *
   * @param bucketName   target bucket's name
   * @param keyName      object's key name
   * @param resourcePath file resource path
   * @return resulting PutObjectResult
   */
  def putObjectFromResources(bucketName: String, keyName: String, resourcePath: String): PutObjectResult =
    s3Client.putObject(bucketName, keyName, new File(getClass.getResource(resourcePath).toURI))

  /**
   * Upload local file to target bucket
   *
   * @param bucketName target bucket's name
   * @param keyName    object's key name
   * @param path       file local path
   * @return resulting PutObjectResult
   */
  def putObject(bucketName: String, keyName: String, path: String): PutObjectResult =
    s3Client.putObject(bucketName, keyName, new File(path))

  def getObjectAsFile(bucketName: String, keyName: String, path: String): File = {
    val s3Object = s3Client.getObject(bucketName, keyName)
    val in: InputStream = s3Object.getObjectContent


    val buffer = Array[Byte](1024.toByte)
    val out: OutputStream = new FileOutputStream(path)

    var length = in.read(buffer)
    while (length != -1) {
      out.write(buffer, 0, length)
      length = in.read(buffer)
    }

    out.close()
    in.close()

    new File(path)
  }
}
