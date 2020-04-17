package wikipipeline

import cats.effect.IO
import cats.implicits._
import utils.FileUtils

object FileDestinationBridge extends DestinationBridge {

  private val CSVHead = "page;views"

  private val statsToCSVLines: IndexedSeq[WikiStat] => IndexedSeq[String] =
    CSVHead +: _.map({ case (page, views) => s"$page;$views" })

  private def writeCSVFile(results: IndexedSeq[WikiStat], date: String): IO[Unit] =
    FileUtils.writeCSV(date ++ ".csv", statsToCSVLines(results))

  def write(date: String)(results: IndexedSeq[WikiStat]): IO[Unit] =
    printResults(results) *>
    writeCSVFile(results, date)
}
