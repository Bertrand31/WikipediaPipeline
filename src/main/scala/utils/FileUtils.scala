package utils

import scala.io.Source
import cats.effect.IO
import sys.process._
import java.net.URL
import java.io.File
import java.util.zip.GZIPInputStream
import java.io.FileInputStream
import java.nio.file.Files
import java.nio.file.Paths
import java.io.FileWriter
import cats.implicits._
import scala.util.Using

object FileUtils {

  def checkIfExists(filename: String): IO[Boolean] =
    IO {
      Files.exists(Paths.get(filename))
    }

  def downloadIfNotExists(url: String, filename: String): IO[Unit] =
    checkIfExists(filename) >>= {
      case true => IO.pure(())
      case _    => IO { new URL(url) #> new File(filename) !! : Unit }
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
