package utils

import scala.io.Source
import cats.effect.IO
import sys.process._
import java.net.URL
import java.io.File
import scala.language.postfixOps
import java.util.zip.GZIPInputStream
import java.io.BufferedInputStream
import java.io.FileInputStream

object FileUtils {

  def downloadFile(url: String, filename: String): IO[Unit] =
    IO {
      new URL(url) #> new File(filename) !!
    }

  def openGZIPFile(filename: String): IO[Iterator[String]] =
    IO {
      Source.fromInputStream(
        new GZIPInputStream(
          new BufferedInputStream(
            new FileInputStream(filename)
          )
        )
      ).getLines
    }
}
