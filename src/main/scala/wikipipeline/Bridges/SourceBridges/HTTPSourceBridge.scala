package wikipipeline.bridges

import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import cats.effect.IO
import cats.implicits._
import utils.FileUtils
import utils.IteratorUtils.ImprovedIterator
import wikipipeline.{AppConfig, BlacklistHandler, WikiStat, WikiStatOrdering}
import BlacklistHandler.isBlacklisted

class HTTPSourceBridge extends SourceBridge {

  // While this looks like an environment variable, it's actually not one: it is unlikely to change
  // and if it did, then the rest of the logic in this file would most likely also have to change.
  // For this reason, it is hardcoded, much like the `getDayURLs` logic which is tightly coupled
  // to that URL structure we depend on.
  private val UrlBase = "https://dumps.wikimedia.org/other/pageviews"

  private val hourFormatter =  DateTimeFormatter ofPattern "HH"
  private val monthFormatter = DateTimeFormatter ofPattern "MM"
  private val dayFormatter =   DateTimeFormatter ofPattern "dd"

  protected def getChunkURL(date: LocalDateTime): String = {
    val year = date.getYear
    val month = date.format(monthFormatter)
    val day = date.format(dayFormatter)
    val hour = date.format(hourFormatter)
    s"$UrlBase/$year/$year-$month/pageviews-$year$month$day-${hour}0000.gz"
  }

  protected val parseWikiStats: Iterator[String] => Iterator[WikiStat] =
    _
      .map(_ split " ")
      .collect({
        case Array(domain, title, count, _) => WikiStat(domain, title, count.toInt)
      })

  protected val makeLocalPath: String => String =
    AppConfig.workingDirectory ++ _.split("/").last

  def getTopNForFile(n: Int)(time: LocalDateTime): IO[Map[String, Seq[WikiStat]]] = {
    val url = getChunkURL(time)
    val filename = makeLocalPath(url)
    FileUtils.download(url, filename) *>
    FileUtils.openGZIPFile(filename)
      .map(parseWikiStats)
      .map(_.getNMostByWithout(n, _.domain, isBlacklisted)(WikiStatOrdering)) <*
    FileUtils.deleteFile(filename)
  }
}
