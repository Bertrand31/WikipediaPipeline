package wikipipeline.bridges

import cats.effect.IO
import wikipipeline.WikiStat

trait DestinationBridge {

  def write(date: String)(results: => IO[IndexedSeq[WikiStat]]): IO[Unit]
}
