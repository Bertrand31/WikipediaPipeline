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

  val unsafeOpenFile: String => Iterator[String] =
    Source.fromFile(_).getLines

  def openGZIPFile(path: String): IO[Iterator[String]] =
    IO {
      Source.fromInputStream(
        new GZIPInputStream(
          new FileInputStream(path)
        )
      ).getLines
    }

  /** We're given a lazy iterator: we might as well leverage its laziness and avoid pulling it all
    * into memory before committing it to the disk. Instead, we're progressively pulling chunks of
    * a configurable size from it, and writing them as we go, in such a way that we never hold more
    * than one chunk in memory.
    */
  def writeCSVProgressively(path: String, iter: => Iterator[_], chunkSize: Int = 5000): IO[Unit] =
    IO {
      Using.resource(new FileWriter(path))(writer =>
        iter
          .sliding(chunkSize, chunkSize)
          .foreach((writer.write(_: String)) compose (_.mkString("\n") :+ '\n'))
      )
    }
}
