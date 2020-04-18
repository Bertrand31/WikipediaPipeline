package wikipipeline.bridges

import java.time.LocalDateTime
import cats.effect.IO
import wikipipeline.WikiStat

trait SourceBridge {

  def getTopNForFile(n: Int)(time: LocalDateTime): IO[Map[String, Seq[WikiStat]]]
}
