package wikipipeline

import cats.effect.IO
import cats.implicits._
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import scala.collection.immutable.ArraySeq
import utils.StringUtils.ImprovedString

object IngestionHandler {

  // While this looks like an environment variable, it's actually not one: it is unlikely to change
  // and if it did, then the rest of the logic in this file would most likely also have to change.
  // For this reason, it is hardcoded, much like the `getDayURLs` logic which is tightly coupled
  // to that URL structure we depend on.
  private val UrlBase = "https://dumps.wikimedia.org/other/pageviews"

  private val hours: List[String] =
    (0 until 24)
      .map(_.toString.padLeft(2, '0') ++ "0000")
      .toList

  private val dayFormatter =   DateTimeFormatter ofPattern "dd"
  private val monthFormatter = DateTimeFormatter ofPattern "MM"
  private val dateFormatter =  DateTimeFormatter ofPattern "yyyy-MM-dd"

  private def getDayURLs(date: LocalDate): List[String] = {
    val year = date.getYear
    val month = date.format(monthFormatter)
    val day = date.format(dayFormatter)
    hours.map(hour => s"$UrlBase/$year/$year-$month/pageviews-$year$month$day-$hour.gz")
  }

  def ingestDay(dayStr: String): IO[Unit] = {
    val day = LocalDate.parse(dayStr)
    val dayId = dateFormatter.format(day)
    FileDestinationBridge.write(dayId) {
      getDayURLs(day)
        .map(HTTPSourceBridge.getTopNForDay(5))
        .sequence
        .map(_.map(_.toMap))
        .map(_ foldMap identity)
        .map(_.to(ArraySeq).sorted(WikiStatOrdering).take(5))
    }
  }
}
