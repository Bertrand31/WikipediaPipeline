package wikipipeline.bridges

import cats.effect.IO
import cats.implicits._
import utils.FileUtils
import wikipipeline.{AppConfig, WikiStat}

object FileDestinationBridge extends DestinationBridge {

  private val CSVHead = "domain;page;views"

  private val statsToCSVLines: Map[String, Seq[WikiStat]] => Seq[String] =
    CSVHead +: _
                .values
                .toList
                .flatMap {
                  _ map { case WikiStat(domain, page, views) => s"$domain;$page;$views" }
                }

  private val makeFilePath: String => String = AppConfig.destinationPath ++ _ ++ ".csv"

  private def writeCSVFile(filePath: String)(results: Map[String, Seq[WikiStat]]): IO[Unit] =
    FileUtils.writeCSV(filePath, statsToCSVLines(results))

  def write(dayId: String)(results: => IO[Map[String, Seq[WikiStat]]]): IO[Unit] = {
    val filePath = makeFilePath(dayId)
    FileUtils.checkIfExists(filePath) >>= {
      case true => IO.pure(())
      case _    => results >>= writeCSVFile(filePath)
    }
  }
}
