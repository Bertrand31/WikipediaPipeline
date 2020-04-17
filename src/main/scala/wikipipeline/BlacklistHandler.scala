package wikipipeline

import scala.util.chaining.scalaUtilChainingOps
import cats.effect.IO
import cats.implicits._
import bloomfilter.mutable.BloomFilter
import utils.FileUtils

object BlacklistHandler {

  private val BlacklistPath = "src/main/resources/data/blacklist_domains_and_pages"

  val bloomFilter = {
    val base = BloomFilter[String](58000, 0.01)
    FileUtils.openFile(BlacklistPath)
      .map(_.foldLeft(base)((bf, item) => bf.tap(_ add item)))
      .unsafeRunSync
  }

  private def askBloomFilter(item: String): Boolean =
    bloomFilter.mightContain(item)

  private def askDisk(item: String): IO[Boolean] =
    FileUtils.openFile(BlacklistPath)
      .map(_.exists(_ === item))

  def isBlacklisted(wikiStat: WikiStat): IO[Boolean] =
    askBloomFilter(wikiStat._1) match {
      case false => IO.pure(false)
      case _ => askDisk(wikiStat._1)
    }
}
