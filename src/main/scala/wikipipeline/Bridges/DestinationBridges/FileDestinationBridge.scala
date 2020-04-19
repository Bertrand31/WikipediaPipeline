package wikipipeline.bridges

import cats.effect.IO
import cats.implicits._
import utils.FileUtils
import utils.IteratorUtils.ImprovedIterator
import wikipipeline.{AppConfig, WikiStat}

class FileDestinationBridge extends DestinationBridge {

  private val CSVHead = "domain;page;views"

  protected val statsToCSVLines: Map[String, Seq[WikiStat]] => Iterator[String] =
    CSVHead +: _
                .values
                .flatten
                .map({ case WikiStat(domain, page, views) => s"$domain;$page;$views" })
                .iterator

  protected val makeFilePath: String => String = AppConfig.destinationPath ++ _ ++ ".csv"

  private def writeCSVFile(filePath: String)(results: Map[String, Seq[WikiStat]]): IO[Unit] =
    FileUtils.writeCSVProgressively(
      filePath,
      statsToCSVLines(results),
    )

  def write(dayId: String)(results: => IO[Map[String, Seq[WikiStat]]]): IO[Unit] = {
    val filePath = makeFilePath(dayId)
    FileUtils.checkIfExists(filePath) >>= {
      case true => IO.pure(())
      case _    => results >>= writeCSVFile(filePath)
    }
  }
}
