package org.tycoon.utils

import com.typesafe.scalalogging.Logger
import org.apache.spark.input.PortableDataStream
import org.apache.spark.rdd.RDD
import org.apache.spark.sql.SparkSession

import java.io.{ByteArrayInputStream, ByteArrayOutputStream, InputStream}
import java.util.zip.ZipInputStream

object ZipUtils {

  // Class logger
  val logger: Logger = Logger(getClass.getName)

  /**
   * Given a tuple with filename and portable data stream (as used in spark binary file streams)
   * returns a list with text content of non-zip files
   *
   * @param portableDataStreamWithName tuple with filename and portable data stream
   * @return list with text content of non-zip files
   */
  def unzip(portableDataStreamWithName: (String, PortableDataStream)): List[String] =
    unzip(portableDataStreamWithName._1, portableDataStreamWithName._2)

  /**
   * Given a tuple with filename and portable data stream (as used in spark binary file streams)
   * and a list with file extensions, returns a list with text content of non-zip files that
   * matches a any file extension
   *
   * @param portableDataStreamWithName tuple with filename and portable data stream
   * @param fileFilter                 list with allowed file extensions, if empty all extensions are allowed
   * @return list with text content of non-zip files that matches a any file extension
   */
  def unzip(
             portableDataStreamWithName: (String, PortableDataStream),
             fileFilter: List[String]
           ): List[String] =
    unzip(portableDataStreamWithName._1, portableDataStreamWithName._2, fileFilter)

  /**
   * Given a filename, a portable data stream (as used in spark binary file streams)
   * and a list with file extensions, returns a list with text content of non-zip files that
   * matches a any file extension
   *
   * @param name               target file name
   * @param portableDataStream target file portable data stream
   * @param fileFilter         list with allowed file extensions, if empty all extensions are allowed
   * @return list with text content of non-zip files that matches a any file extension
   */
  def unzip(
             name: String,
             portableDataStream: PortableDataStream,
             fileFilter: List[String] = List.empty
           ): List[String] = {

    // Avoid open stream when data shouldn't be extracted
    if (!name.endsWith(".zip") && // No .zip file
      fileFilter.nonEmpty && // Exists file filter
      !fileFilter.exists(name.endsWith)) // No extension matches the current file name
      return List.empty

    // Open stream
    val dataInputStream = portableDataStream.open()

    // Get data from stream
    val extractedText = name match {
      // If fileName is another zipped file, recursively extract it
      case fileName if fileName.endsWith(".zip") =>
        readZipInputStream(dataInputStream, fileFilter).map(_.toString)
      // Otherwise it is assumed to be a text file with a valid extension
      case _ =>
        List(scala.io.Source.fromInputStream(portableDataStream.open()).getLines().mkString("\n"))
    }

    // Close stream
    dataInputStream.close()

    extractedText
  }

  /**
   * Given an zip input stream, extracts recursively all text files matching allowed file extensions
   *
   * @param inputStream target zip input stream
   * @param fileFilter  list with allowed file extensions, if empty all extensions are allowed
   * @return list with all allowed files as ByteOutputArrayStream
   */
  def readZipInputStream(
                          inputStream: InputStream,
                          fileFilter: List[String] = List.empty
                        ): List[ByteArrayOutputStream] = {
    val zis = new ZipInputStream(inputStream)

    LazyList.continually(zis.getNextEntry)
      .takeWhile {
        case null =>
          zis.close()
          inputStream.close()
          false
        case _ => true
      }
      .flatMap(entry => {
        val outStream = new ByteArrayOutputStream()
        val buffer = Array[Byte](1024.toByte)
        var length = zis.read(buffer)
        while (length != -1) {
          outStream.write(buffer, 0, length)
          length = zis.read(buffer)
        }
        outStream.close()

        println(entry.getName)
        System.gc()

        (entry.getName, entry.isDirectory) match {
          // Zip files
          case (name, false) if name.endsWith(".zip") =>
            val inStream: ByteArrayInputStream = new ByteArrayInputStream(outStream.toByteArray)
            readZipInputStream(inStream)
          // When no fileFilter defined or file match at least one fileFilter entry
          case (name, false) if fileFilter.isEmpty || fileFilter.exists(name.endsWith) =>
            List(outStream)
          // Directories or unsupported cases
          case (_, true) | _ => List.empty
        }
      }).toList
  }

  implicit class SparkSessionZipExtensions(spark: SparkSession) {
    def readZippedTextFiles(
                             searchPath: String,
                             fileFilter: List[String] = List.empty
                           ): RDD[String] =
      spark.sparkContext
        .binaryFiles(searchPath)
        .flatMap(target => ZipUtils.unzip(target, fileFilter))
  }
}
