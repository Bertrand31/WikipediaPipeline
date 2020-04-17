package wikipipeline

import cats.implicits._
import cats.effect.IO

trait DestinationBridge {

  private def ioPrint(str: String): IO[Unit] = IO { println(str) }

  final protected def printResults(results: IndexedSeq[WikiStat]): IO[Unit] = {
    val str =
      results
        .map({ case (page, count) => s"'$page' was seen $count times\n" })
        .mkString
    ioPrint(s"\n======= Top ${results.size} most seen pages =======\n") *>
    ioPrint(str)
  }

  def write(date: String)(results: IndexedSeq[WikiStat]): IO[Unit]
}
