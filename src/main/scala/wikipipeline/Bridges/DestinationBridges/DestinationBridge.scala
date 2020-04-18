package wikipipeline.bridges

import cats.effect.IO
import wikipipeline.WikiStat

trait DestinationBridge {

  def write(dayId: String)(results: => IO[Map[String, Seq[WikiStat]]]): IO[Unit]
}
