package wikipipeline

import scala.util.Failure
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import cats.effect.IO
import cats.implicits._
import utils.DateUtils.{getHoursBetween, parseDate}
import bridges.{DestinationBridge, SourceBridge}

class IngestionHandler(sourceBridge: SourceBridge, destinationBridge: DestinationBridge) {

  private val generateOutputID: LocalDateTime => String =
    DateTimeFormatter.ofPattern("yyyy-MM-dd-HH").format

  def ingestHour(time: LocalDateTime): IO[Unit] =
    destinationBridge.write(generateOutputID(time)) {
      sourceBridge.getTopNForFile(AppConfig.topNumber)(time)
    }

  def ingestRange(arguments: List[String]): IO[Unit] =
    {
      arguments match {
        case start +: end +: _ =>
          (parseDate(start), parseDate(end))
            .mapN(getHoursBetween(_, _))
        case _ =>
          // For now we'll simply discard this error and fallback. In the future,
          // we might want a different behavior, or simply introducing logging.
          Failure(new IllegalArgumentException(s"Not enough dates provided"))
      }
    }
      .getOrElse {
          val now = LocalDateTime.now
          getHoursBetween(now.minusDays(1), now)
      }
      .foldMap(ingestHour)
}
