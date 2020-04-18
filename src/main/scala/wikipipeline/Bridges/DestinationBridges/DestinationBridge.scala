package wikipipeline.bridges

import cats.effect.IO
import wikipipeline.WikiStat

trait DestinationBridge {

  def write(date: String)(results: => IO[Seq[WikiStat]]): IO[Unit]
}
