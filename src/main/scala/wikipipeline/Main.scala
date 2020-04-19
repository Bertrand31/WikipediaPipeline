package wikipipeline

import java.time.LocalDateTime
import cats.data.NonEmptyList
import cats.effect.{ExitCode, IO, IOApp}
import cats.implicits._
import bridges.{FileDestinationBridge, HTTPSourceBridge}

object Main extends IOApp {

  // Here, we decide which storage mediums we'll be using to read and write the data.
  // In our case, we're download data over HTTP, and writing it to a simple file.
  private val ingestionHandler =
    new IngestionHandler(new HTTPSourceBridge, new FileDestinationBridge)

  def run(args: List[String]): IO[ExitCode] =
    args
      .map(LocalDateTime.parse)
      .toNel
      // It an empty list was provided, we use the current time minus 24 hours
      .getOrElse(NonEmptyList.of(LocalDateTime.now.minusDays(1)))
      .foldMap(ingestionHandler.ingestHourRange)
      .as(ExitCode.Success)
}
