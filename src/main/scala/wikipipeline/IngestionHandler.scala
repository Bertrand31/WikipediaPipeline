package wikipipeline

import scala.util.Try
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import cats.effect.IO
import cats.implicits._
import bridges.{DestinationBridge, SourceBridge}

class IngestionHandler(sourceBridge: SourceBridge, destinationBridge: DestinationBridge) {

  private val generateOutputID: LocalDateTime => String =
    DateTimeFormatter.ofPattern("yyyy-MM-dd-HH").format

  def ingestHour(time: LocalDateTime): IO[Unit] =
    destinationBridge.write(generateOutputID(time)) {
      sourceBridge.getTopNForFile(AppConfig.topNumber)(time)
    }

  private def getHoursBetween(
    start: LocalDateTime, end: LocalDateTime, soFar: List[LocalDateTime] = List.empty,
  ): List[LocalDateTime] =
    if (start.isEqual(end) || start.isAfter(end)) soFar.reverse
    else getHoursBetween(start.plusHours(1), end, start +: soFar)

  def ingestRange(arguments: List[String]): IO[Unit] =
    Try {
      val start +: end +: _ = arguments
      val startDate = LocalDateTime.parse(start)
      val endDate = LocalDateTime.parse(end)
      getHoursBetween(startDate, endDate)
    }
      .getOrElse {
        val now = LocalDateTime.now
        getHoursBetween(now.minusDays(1), now)
      }
      .foldMap(ingestHour)
}
