package wikipipeline

import cats.effect.IO
import cats.implicits._
import utils.FileUtils

object HTTPSourceBridge extends SourceBridge {

  private val parseWikiStats: Iterator[String] => Iterator[WikiStat] =
    _
      .map(_ split " ")
      .collect({
        case Array(domain, title, count, _) => (domain ++ " " ++ title, count.toInt)
      })

  private val makeLocalPath: String => String =
    _
      .split("/")
      .last

  def getTopNForDay(n: Int)(url: String): IO[Map[String, Int]] = {
    val filename = makeLocalPath(url)
    FileUtils.download(url, filename) *>
    FileUtils.openGZIPFile(filename)
      .map(parseWikiStats)
      .map(WikiStatHandler.getNMost(n)) <*
    FileUtils.deleteFile(filename)
  }
}
