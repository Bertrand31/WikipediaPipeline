package wikipipeline

import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import cats.effect.IO
import bridges.{DestinationBridge, SourceBridge}

class IngestionHandler(sourceBridge: SourceBridge, destinationBridge: DestinationBridge) {

  private val generateOutputID: LocalDateTime => String =
    DateTimeFormatter.ofPattern("yyyy-MM-dd-HH").format

  def ingestHourRange(time: LocalDateTime): IO[Unit] =
    destinationBridge.write(generateOutputID(time)) {
      sourceBridge.getTopNForFile(AppConfig.topNumber)(time)
    }
}
