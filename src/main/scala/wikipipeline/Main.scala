package wikipipeline

import java.time.LocalDateTime
import cats.data.NonEmptyList
import cats.effect.{ExitCode, IO, IOApp}
import cats.implicits._

object Main extends IOApp {

  def run(args: List[String]): IO[ExitCode] =
    NonEmptyList.fromList(args map LocalDateTime.parse)
      .getOrElse(NonEmptyList.of(LocalDateTime.now.minusDays(1)))
      .foldMap(IngestionHandler.ingestHourRange)
      .as(ExitCode.Success)
}
