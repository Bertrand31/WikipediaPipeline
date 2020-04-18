package wikipipeline

import cats.effect.IO
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import bridges.{FileDestinationBridge, HTTPSourceBridge}

object IngestionHandler {

  // While this looks like an environment variable, it's actually not one: it is unlikely to change
  // and if it did, then the rest of the logic in this file would most likely also have to change.
  // For this reason, it is hardcoded, much like the `getDayURLs` logic which is tightly coupled
  // to that URL structure we depend on.
  private val UrlBase = "https://dumps.wikimedia.org/other/pageviews"

  private val hourFormatter =  DateTimeFormatter ofPattern "hh"
  private val dayFormatter =   DateTimeFormatter ofPattern "dd"
  private val monthFormatter = DateTimeFormatter ofPattern "MM"
  private val dateFormatter =  DateTimeFormatter ofPattern "yyyy-MM-dd:hh"

  private def getDateURL(date: LocalDateTime): String = {
    val year = date.getYear
    val month = date.format(monthFormatter)
    val day = date.format(dayFormatter)
    val hour = date.format(hourFormatter)
    s"$UrlBase/$year/$year-$month/pageviews-$year$month$day-${hour}0000.gz"
  }

  def ingestHourRange(time: LocalDateTime): IO[Unit] =
    FileDestinationBridge.write(dateFormatter.format(time)) {
      HTTPSourceBridge.getTopNForFile(AppConfig.topNumber)(getDateURL(time))
        .map(
          _
            .sorted(WikiStatOrdering)
            .take(AppConfig.topNumber)
        )
    }
}
