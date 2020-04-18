package wikipipeline.bridges

import cats.effect.IO
import wikipipeline.WikiStat

trait SourceBridge {

  def getTopNForDay(n: Int)(day: String): IO[Seq[WikiStat]]
}
