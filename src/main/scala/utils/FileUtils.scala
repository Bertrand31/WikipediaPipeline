package utils

import sys.process._
import scala.language.postfixOps
import scala.io.Source
import scala.util.Using
import java.net.URL
import java.nio.file.{Files, Paths}
import java.util.zip.GZIPInputStream
import java.io.{File, FileWriter, FileInputStream}
import cats.effect.IO

object FileUtils {

  def deleteFile(path: String): IO[Unit] =
    IO {
      new File(path).delete() : Unit
    }

  def checkIfExists(path: String): IO[Boolean] =
    IO {
      Files.exists(Paths.get(path))
    }

  def download(url: String, filename: String): IO[Unit] =
    IO {
      new URL(url) #> new File(filename) !! : Unit
    }

  def openGZIPFile(filename: String): IO[Iterator[String]] =
    IO {
      Source.fromInputStream(
        new GZIPInputStream(
          new FileInputStream(filename)
        )
      ).getLines
    }

  def writeCSV(path: String, data: => IterableOnce[String]): IO[Unit] =
    IO {
      Using.resource(new FileWriter(path)) {
        _.write(data.iterator.mkString("\n") :+ '\n')
      }
    }
}
