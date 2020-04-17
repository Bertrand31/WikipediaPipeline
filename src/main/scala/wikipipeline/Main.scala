package wikipipeline

import cats.effect._
import cats.implicits._
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import scala.collection.immutable.ArraySeq

object Main extends IOApp {

  private val hours: List[String] =
    (0 until 24)
      .map(_.toString.reverse.padTo(2, "0").reverse.mkString)
      .map(_ ++ "0000")
      .toList

  private val UrlBase = "https://dumps.wikimedia.org/other/pageviews"

  private val dateFormatter = DateTimeFormatter ofPattern "yyyy-MM-dd"
  private val monthFormatter = DateTimeFormatter ofPattern "MM"
  private val dayFormatter = DateTimeFormatter ofPattern "dd"

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
          // TODO: Avoid work if result file exists
        getDayURLs(day)
          .map(HTTPSourceBridge.getTopNForDay(5))
          .sequence
          .map(_ foldMap identity)
          .map(_.to(ArraySeq).sorted(WikiStatOrdering).take(5))
          .flatMap(FileDestinationBridge.write(dateFormatter.format(day)))
      )
      .sequence
      .as(ExitCode.Success)
}
