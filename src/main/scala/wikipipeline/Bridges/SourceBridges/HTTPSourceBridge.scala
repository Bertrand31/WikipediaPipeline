package wikipipeline

import cats.effect.IO
import cats.implicits._
import utils.FileUtils

object HTTPSourceBridge extends SourceBridge {

  private def parseLine(line: String): Option[WikiStat] =
    line.split(" ") match {
       case Array(domain, title, count, _) => Some((domain + " " + title, count.toInt))
       case _                              => None
    }

  def read(url: String): IO[Iterator[WikiStat]] = {
    val filename = url.split("/").last
    FileUtils.downloadIfNotExists(url, filename) *>
    FileUtils.openGZIPFile(filename)
      .map(_ flatMap parseLine)
  }
}
