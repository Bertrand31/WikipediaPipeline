package wikipipeline.bridges

import cats.effect.IO
import cats.implicits._
import utils.FileUtils
import utils.IteratorUtils.ImprovedIterator
import wikipipeline.{AppConfig, BlacklistHandler, WikiStat}
import wikipipeline.WikiStatOrdering
import BlacklistHandler.isBlacklisted

object HTTPSourceBridge extends SourceBridge {

  private val parseWikiStats: Iterator[String] => Iterator[WikiStat] =
    _
      .map(_ split " ")
      .collect({
        case Array(domain, title, count, _) => WikiStat(domain, title, count.toInt)
      })

  private val makeLocalPath: String => String =
    AppConfig.workingDirectory ++ _.split("/").last

  def getTopNForFile(n: Int)(url: String): IO[Map[String, Seq[WikiStat]]] = {
    val filename = makeLocalPath(url)
    FileUtils.download(url, filename) *>
    FileUtils.openGZIPFile(filename)
      .map(parseWikiStats)
      .map(_.getNMostByWithout(n, _.domain, isBlacklisted)(WikiStatOrdering)) <*
    FileUtils.deleteFile(filename)
  }
}
