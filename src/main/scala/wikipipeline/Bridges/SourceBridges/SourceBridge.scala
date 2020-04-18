package wikipipeline.bridges

import cats.effect.IO
import wikipipeline.WikiStat

trait SourceBridge {

  def getTopNForFile(n: Int)(url: String): IO[Seq[WikiStat]]
}
