package wikipipeline

import cats.effect.IO
import cats.implicits._
import utils.FileUtils

object FileDestinationBridge extends DestinationBridge {

  private val CSVHead = "page;views"

  private val makeFilePath: String => String = AppConfig.destinationPath ++ _ ++ ".csv"

  private val statsToCSVLines: IndexedSeq[WikiStat] => IndexedSeq[String] =
    CSVHead +: _.map({ case (page, views) => s"$page;$views" })

  private def writeCSVFile(filePath: String)(results: IndexedSeq[WikiStat]): IO[Unit] =
    FileUtils.writeCSV(filePath, statsToCSVLines(results))

  def write(dayId: String)(results: => IO[IndexedSeq[WikiStat]]): IO[Unit] = {
    val filePath = makeFilePath(dayId)
    FileUtils.checkIfExists(filePath) >>= {
      case true => IO.pure(())
      case _    => results >>= writeCSVFile(filePath)
    }
  }
}
