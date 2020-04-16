package wikipipeline

import cats.effect.IO

trait SourceBridge {

  def read(day: String): IO[Iterator[WikiStat]]
}
