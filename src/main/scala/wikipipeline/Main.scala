package airportmatching

import cats.effect._
import cats.implicits._
import utils.FileUtils
import scala.collection.mutable.PriorityQueue
import scala.util.hashing.MurmurHash3.stringHash
import java.time.LocalDate
import java.time.format.DateTimeFormatter

object Main extends IOApp {

  private def parseLine(line: String): (String, Int) = {
    val Array(domain, title, count, _) = line.split(" ")
    (domain + "|" + title, count.toInt)
  }

  type Item = (String, Int)

  object ItemOrdering extends Ordering[Item] {
    def compare(a: Item, b: Item) = b._2 compare a._2
  }

  private def getNMost(iter: Iterator[Item], n: Int): Map[String, Int] = {
    val nFirst = iter.take(n)
    val heap = PriorityQueue.empty(ItemOrdering).addAll(nFirst)
    iter
      .foldLeft(heap)((heap, line) => {
        val (_, count) = line
        if (count <= heap.head._2) heap
        else {
          heap.dequeue()
          heap.enqueue(line)
          heap
        }
      })
      .toMap
  }

  private val hours: Seq[String] =
    (0 until 24)
      .map(_.toString.reverse.padTo(2, "0").reverse.mkString)
      .map(_ ++ "0000")

  private val UrlBase = "https://dumps.wikimedia.org/other/pageviews"

  private val monthFormatter = DateTimeFormatter.ofPattern("MM")
  private val dayFormatter = DateTimeFormatter.ofPattern("dd")

  private def getDayURLs(date: LocalDate): Seq[String] = {
    val year = date.getYear
    val month = date.format(monthFormatter)
    val day = date.format(dayFormatter)
    hours.map(hour => s"$UrlBase/$year/$year-$month/pageviews-$year$month$day-$hour.gz")
  }

  private val getDate: String => LocalDate = LocalDate.parse

  def run(args: List[String]): IO[ExitCode] = {
    require(!args.isEmpty)
    args
      .map(getDate)
      .flatMap(getDayURLs)
      .map(url => {
        val filename = stringHash(url).toString ++ ".gz"
        (FileUtils.downloadFile(url, filename) *> FileUtils.openGZIPFile(filename))
          .map(iter => getNMost(iter.map(parseLine), 5))
      })
      .toList
      .sequence
      .map(_ foldMap identity)
      .map(count => {
        println(count)
        count
      })
      .as(ExitCode.Success)
  }
}
