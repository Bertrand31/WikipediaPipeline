package airportmatching

import scala.util.chaining.scalaUtilChainingOps
import cats.effect._
import cats.implicits._
import utils.FileUtils
import scala.collection.mutable.PriorityQueue
import scala.util.hashing.MurmurHash3.stringHash
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import scala.collection.immutable.ArraySeq

object Main extends IOApp {

  type Item = (String, Int)

  object ItemOrdering extends Ordering[Item] {
    def compare(a: Item, b: Item) = b._2 compare a._2
  }

  private def parseLine(line: String): Option[Item] =
    line.split(" ") match {
       case Array(domain, title, count, _) => Some((domain + " " + title, count.toInt))
       case _                              => None
    }

  private def getNMost(n: Int)(iter: Iterator[Item]): PriorityQueue[Item] = {
    val baseHeap =
      PriorityQueue
        .empty(ItemOrdering)
        .addAll(iter take n)
    if (!iter.hasNext)
      baseHeap
    else
      iter
        .foldLeft(baseHeap)((heap, line) =>
          if (line._2 <= heap.head._2)
            heap
          else
            heap
              .tap(_.dequeue)
              .tap(_.enqueue(line))
        )
  }

  private val hours: Seq[String] =
    (0 until 24)
      .map(_.toString.reverse.padTo(2, "0").reverse.mkString)
      .map(_ ++ "0000")

  private val UrlBase = "https://dumps.wikimedia.org/other/pageviews"

  private val monthFormatter = DateTimeFormatter ofPattern "MM"
  private val dayFormatter = DateTimeFormatter ofPattern "dd"

  private def getDayURLs(date: LocalDate): Seq[String] = {
    val year = date.getYear
    val month = date.format(monthFormatter)
    val day = date.format(dayFormatter)
    hours.map(hour => s"$UrlBase/$year/$year-$month/pageviews-$year$month$day-$hour.gz")
  }

  private val getDate: String => LocalDate = LocalDate.parse

  private def ioPrint(str: String): IO[Unit] = IO { println(str) }

  private def printResults(results: IndexedSeq[Item]): IO[Unit] = {
    val str = results.map({
      case (title, count) => s""""$title" was seen $count times\n"""
    }).mkString
    ioPrint(s"\n======= Top ${results.size} most seen pages =======\n") *>
    ioPrint(str)
  }

  def run(args: List[String]): IO[ExitCode] =
    args
      .map(getDate)
      .flatMap(getDayURLs)
      .map(url => {
        val filename = stringHash(url).toString ++ ".gz"
        FileUtils.downloadIfNotExists(url, filename) *>
        FileUtils.openGZIPFile(filename)
          .map(_ flatMap parseLine)
          .map(getNMost(5))
      })
      .parSequence
      .map(_.map(_.dequeueAll.toMap))
      .map(_ foldMap identity)
      .map(_.to(ArraySeq).sorted(ItemOrdering).take(5))
      .flatMap(printResults)
      .as(ExitCode.Success)
}
