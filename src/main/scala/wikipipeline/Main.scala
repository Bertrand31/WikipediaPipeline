package wikipipeline

import cats.effect.{ExitCode, IO, IOApp}
import cats.implicits._
import bridges.{FileDestinationBridge, HTTPSourceBridge}

object Main extends IOApp {

  // Here, we decide which storage mediums we'll be using to read and write the data.
  // In our case, we're download data over HTTP, and writing it to a simple file.
  private val ingestionHandler =
    new IngestionHandler(new HTTPSourceBridge, new FileDestinationBridge)

  def run(args: List[String]): IO[ExitCode] =
    ingestionHandler
      .ingestRange(args)
      .as(ExitCode.Success)
}
