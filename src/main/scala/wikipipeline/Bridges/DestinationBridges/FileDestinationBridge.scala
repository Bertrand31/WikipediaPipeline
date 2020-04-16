package wikipipeline

import cats.implicits._
import cats.effect.IO
import utils.FileUtils

object FileDestinationBridge extends DestinationBridge {

  private def ioPrint(str: String): IO[Unit] = IO { println(str) }

  private def printResults(results: IndexedSeq[WikiStat]): IO[Unit] = {
    val str =
      results
        .map({ case (page, count) => s"'$page' was seen $count times\n" })
        .mkString
    ioPrint(s"\n======= Top ${results.size} most seen pages =======\n") *>
    ioPrint(str)
  }

  private val CSVHead = "page;views"

  private val statsToCSVLines: IndexedSeq[WikiStat] => IndexedSeq[String] =
    CSVHead +: _.map({ case (page, views) => s"$page;$views" })

  private def writeCSVFile(results: IndexedSeq[WikiStat], date: String): IO[Unit] =
    FileUtils.writeCSV(date ++ ".csv", statsToCSVLines(results))

  def write(date: String)(results: IndexedSeq[WikiStat]): IO[Unit] =
    printResults(results) *> writeCSVFile(results, date)
}
