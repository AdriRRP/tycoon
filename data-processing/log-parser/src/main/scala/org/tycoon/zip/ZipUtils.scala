package org.tycoon.zip

import org.apache.spark.input.PortableDataStream

import java.io.{BufferedReader, ByteArrayInputStream, ByteArrayOutputStream, InputStream, InputStreamReader}
import java.util.stream.Collectors
import java.util.zip.{ZipEntry, ZipInputStream}

object ZipUtils {
  def unzip(portableDataStreamWithName: (String, PortableDataStream)): List[String] =
    unzip(portableDataStreamWithName._1, portableDataStreamWithName._2)

  def unzip(name: String, portableDataStream: PortableDataStream): List[String] = {
    name match {
      // If file is another zipped file, recursively extract it
      case file if file.endsWith(".zip") =>
        readZipInputStream(portableDataStream.open()).map(_.toString())
      // If file is a text file, return content in a list
      case file if file.endsWith(".txt") =>
        List(scala.io.Source.fromInputStream(portableDataStream.open()).getLines().mkString("\n"))
      // Other cases ignore
      case _ => List.empty
    }
  }

  def readZipInputStream(inputStream: InputStream): List[ByteArrayOutputStream] = {
    val zis = new ZipInputStream(inputStream)

    LazyList.continually(zis.getNextEntry)
      .takeWhile {
        case null => zis.close(); false
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
        if (entry.getName.endsWith(".zip")) {
          val inStream: ByteArrayInputStream  = new ByteArrayInputStream(outStream.toByteArray);
          readZipInputStream(inStream);
        } else {
          List(outStream)
        }
      }).toList
  }


  /*
    implicit class ZipSparkContext(val sc: SparkContext) extends AnyVal {

      def readFile(path: String,
                   minPartitions: Int = sc.defaultMinPartitions): RDD[String] = {

        if (path.endsWith(".zip")) {
          sc.binaryFiles(path, minPartitions)
            .flatMap { case (name: String, content: PortableDataStream) =>
              val zis = new ZipInputStream(content.open)
              LazyList.continually(zis.getNextEntry)
                .takeWhile {
                  case null => zis.close(); false
                  case _ => true
                }
                .map { _ =>
                  val br = new BufferedReader(new InputStreamReader(zis))
                  LazyList.continually(br.readLine()).takeWhile(_ != null)
                  scala.io.Source.fromInputStream(zis, "UTF-8").getLines.mkString("\n")
                }
            }
        } else {
          sc.textFile(path, minPartitions)
        }
      }
    }*/


}
