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

  private val makeCSVFilename: String => String =
    _
      .split("/")
      .last

  def read(url: String): IO[Iterator[WikiStat]] = {
    val filename = makeCSVFilename(url)
    FileUtils.downloadIfNotExists(url, filename) *>
    FileUtils.openGZIPFile(filename).map(parseWikiStats)
  }
}
