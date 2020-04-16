package wikipipeline

import cats.effect.IO

trait DestinationBridge {

  def write(date: String)(results: IndexedSeq[WikiStat]): IO[Unit]
}
