package wikipipeline

import scala.util.chaining.scalaUtilChainingOps
import cats.implicits._
import bloomfilter.mutable.BloomFilter
import utils.FileUtils

/** Right now, the amount of data we have in our blacklist is relatively small.
  * Because of that, we could simply load this data into a HashSet and hold it in memory for the
  * lifetime of this app. If, at some point, the data became too much, we could hash every item in
  * order for it to occupy less space (at the cost of a probably negligible amount of runtime
  * speed).
  *
  * However here, in order to demonstrate how we'd do if we were to tackle a massive amount of data
  * in the blacklist, we're using a Bloom Filter to check whether an element is definitely not
  * blacklisted, or may be. If it is, we simply go open the blacklist file on the disk to check.
  *
  * An even better version of this could, for example, slice the blacklist file into smaller files
  * on the disk, based for example on the hash of the first letter of every blacklist item.
  * This way, every time we'd get a positive match from the bloom filter, we'd only open a -much-
  * smaller file and go through a lot less data to check if this wasn't a false positive.
  *
  * There are many ways to achieve this ; what you will find below is a balance between being able
  * to handle very large dataset and keeping the implementation simple.
  */
object BlacklistHandler {

  // 58000 is approximately the number of blacklisted pages we have at the moment.
  val bloomFilter = {
    val base = BloomFilter[String](58000, 0.01)
    FileUtils.unsafeOpenFile(AppConfig.blacklistPath)
      .foldLeft(base)((bf, item) => bf.tap(_ add item))
  }

  private val bloomFilterContains: String => Boolean =
    bloomFilter.mightContain

  private def diskContains(item: String): Boolean =
    FileUtils.unsafeOpenFile(AppConfig.blacklistPath).exists(_ === item)

  def isBlacklisted(wikiStat: WikiStat): Boolean =
    bloomFilterContains(wikiStat._1) && diskContains(wikiStat._1)
}
