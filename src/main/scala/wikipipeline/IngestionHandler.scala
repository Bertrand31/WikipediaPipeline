package wikipipeline

import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import cats.effect.IO
import bridges.{FileDestinationBridge, HTTPSourceBridge}

object IngestionHandler {

  private val generateOutputID: LocalDateTime => String =
    DateTimeFormatter.ofPattern("yyyy-MM-dd-HH").format

  def ingestHourRange(time: LocalDateTime): IO[Unit] =
    FileDestinationBridge.write(generateOutputID(time)) {
      HTTPSourceBridge.getTopNForFile(AppConfig.topNumber)(time)
    }
}
