package airportmatching

import cats.Monoid
import cats.effect._
import cats.implicits._
import utils.FileUtils
import scala.collection.mutable.PriorityQueue
import scala.util.hashing.MurmurHash3.stringHash
import java.time.LocalDate
import java.time.format.DateTimeFormatter

object Main extends IOApp {

  type Item = (String, Int)

  object ItemOrdering extends Ordering[Item] {
    def compare(a: Item, b: Item) = b._2 compare a._2
  }

  implicit def itemQueueMonoid = new Monoid[PriorityQueue[Item]] {

      override def empty: PriorityQueue[Item] = PriorityQueue.empty[Item](Ordering[Item])

      override def combine(x: PriorityQueue[Item], y: PriorityQueue[Item]): PriorityQueue[Item] =
        x ++ y
  }

  private def parseLine(line: String): Option[Item] =
    line.split(" ") match {
       case Array(domain, title, count, _) => Some((domain + " " + title, count.toInt))
       case _                              => None
    }

  private def getNMost(n: Int)(iter: Iterator[Item]): PriorityQueue[Item] = {
    val nFirst = iter.take(n)
    val heap = PriorityQueue.empty(ItemOrdering).addAll(nFirst)
    iter
      .foldLeft(heap)((heap, line) => {
        val (_, count) = line
        if (count <= heap.head._2) heap
        else {
          heap.dequeue
          heap.enqueue(line)
          heap
        }
      })
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

  def run(args: List[String]): IO[ExitCode] = {
    require(!args.isEmpty)
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
      .sequence
      .map(_ foldMap identity)
      .map(_ takeRight 5)
      .map(count => {
        println(count)
        count
      })
      .as(ExitCode.Success)
  }
}
