package wikipipeline.bridges

import cats.effect.IO
import cats.implicits._
import utils.FileUtils
import wikipipeline.{AppConfig, WikiStat}

object FileDestinationBridge extends DestinationBridge {

  private val CSVHead = "domain;page;views"

  private val makeFilePath: String => String = AppConfig.destinationPath ++ _ ++ ".csv"

  private val statsToCSVLines: Seq[WikiStat] => Seq[String] =
    CSVHead +: _.map({ case WikiStat(domain, page, views) => s"$domain;$page;$views" })

  private def writeCSVFile(filePath: String)(results: Seq[WikiStat]): IO[Unit] =
    FileUtils.writeCSV(filePath, statsToCSVLines(results))

  def write(dayId: String)(results: => IO[Seq[WikiStat]]): IO[Unit] = {
    val filePath = makeFilePath(dayId)
    FileUtils.checkIfExists(filePath) >>= {
      case true => IO.pure(())
      case _    => results >>= writeCSVFile(filePath)
    }
  }
}
