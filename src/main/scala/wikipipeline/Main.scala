package wikipipeline

import cats.effect._
import cats.implicits._
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import scala.collection.immutable.ArraySeq
import utils.StringUtils._

object Main extends IOApp {

  private val hours: List[String] =
    (0 until 24)
      .map(_.toString.padLeft(2, '0') ++ "0000")
      .toList

  private val UrlBase = "https://dumps.wikimedia.org/other/pageviews"

  private val dayFormatter =   DateTimeFormatter ofPattern "dd"
  private val monthFormatter = DateTimeFormatter ofPattern "MM"
  private val dateFormatter =  DateTimeFormatter ofPattern "yyyy-MM-dd"

  private def getDayURLs(date: LocalDate): List[String] = {
    val year = date.getYear
    val month = date.format(monthFormatter)
    val day = date.format(dayFormatter)
    hours.map(hour => s"$UrlBase/$year/$year-$month/pageviews-$year$month$day-$hour.gz")
  }

  def run(args: List[String]): IO[ExitCode] =
    args
      .map(LocalDate.parse)
      .map(day =>
        FileDestinationBridge.write(dateFormatter.format(day)) {
          getDayURLs(day)
            .map(HTTPSourceBridge.getTopNForDay(5))
            .sequence
            .map(_ foldMap identity)
            .map(_.to(ArraySeq).sorted(WikiStatOrdering).take(5))
        }
      )
      .sequence
      .as(ExitCode.Success)
}
