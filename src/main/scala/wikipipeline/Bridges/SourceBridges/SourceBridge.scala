package wikipipeline

import cats.effect.IO

trait SourceBridge {

  def getTopNForDay(n: Int)(day: String): IO[Map[String, Int]]
}
