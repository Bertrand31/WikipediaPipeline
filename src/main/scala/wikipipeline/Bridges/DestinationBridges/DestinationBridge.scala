package wikipipeline

import cats.effect.IO

trait DestinationBridge {

  def write(date: String)(results: => IO[IndexedSeq[WikiStat]]): IO[Unit]
}
