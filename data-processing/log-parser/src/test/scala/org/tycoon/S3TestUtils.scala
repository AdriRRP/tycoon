package org.tycoon

import com.amazonaws.auth.DefaultAWSCredentialsProviderChain
import com.amazonaws.client.builder.AwsClientBuilder
import com.amazonaws.regions.Regions
import com.amazonaws.services.s3.{AmazonS3, AmazonS3ClientBuilder}
import com.amazonaws.services.s3.model.{Bucket, ListVersionsRequest, PutObjectResult, VersionListing}
import com.typesafe.scalalogging.Logger

import java.io.File
import scala.util.{Failure, Success, Try}
import scala.util.control.Breaks.break
import scala.jdk.CollectionConverters._

trait S3TestUtils {

  // Class logger
  val logger: Logger = Logger(getClass.getName)

  lazy val s3: AmazonS3 = AmazonS3ClientBuilder
    .standard()
    .withRegion(Regions.DEFAULT_REGION)
    .withCredentials(new DefaultAWSCredentialsProviderChain())
    .withEndpointConfiguration(
      new AwsClientBuilder.EndpointConfiguration(
        "https://localhost:9000",
        Regions.DEFAULT_REGION.getName)
    )
    .build()

  def createBucket(bucketName: String): Option[Bucket] = {
    if (s3.doesBucketExistV2(bucketName)) {
      logger.warn(s"Bucket $bucketName already exists.")
      getBucket(bucketName);
    } else {
      Try(s3.createBucket(bucketName)) match {
        case Success(bucket) => Some(bucket)
        case Failure(exception) =>
          logger.error(exception.toString)
          None
      }
    }
  }

  def getBucket(bucketName: String): Option[Bucket] =
    s3.listBuckets().asScala.toList.find(_.getName == bucketName)

  def deleteBucket(bucketName: String): Unit = {
    var objectListing = s3.listObjects(bucketName)
    while (true) {
      objectListing.getObjectSummaries.iterator().asScala.toList
        .foreach(obj => s3.deleteObject(bucketName, obj.getKey))

      if (objectListing.isTruncated)
        objectListing = s3.listNextBatchOfObjects(objectListing)
      else
        break
    }

    // Delete all object versions (required for versioned buckets).
    var versionList: VersionListing = s3.listVersions(new ListVersionsRequest().withBucketName(bucketName))
    while (true) {
      versionList.getVersionSummaries.iterator().asScala.toList
        .foreach(version => s3.deleteVersion(bucketName, version.getKey, version.getVersionId))

      if (versionList.isTruncated)
        versionList = s3.listNextBatchOfVersions(versionList);
      else
        break
    }

    // After all objects and object versions are deleted, delete the bucket.
    s3.deleteBucket(bucketName);
  }

  def putObjectFromResources(bucketName: String, keyName: String, resourcePath: String): PutObjectResult =
    s3.putObject(bucketName, keyName, new File(getClass.getResource(resourcePath).toURI))

  def putObject(bucketName: String, keyName: String, path: String): PutObjectResult =
    s3.putObject(bucketName, keyName, new File(path))
}
