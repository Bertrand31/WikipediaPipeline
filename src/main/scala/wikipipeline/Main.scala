package airportmatching

import cats.effect._
import cats.implicits._
import utils.FileUtils
import scala.collection.mutable.PriorityQueue
import scala.util.hashing.MurmurHash3.stringHash

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

  private val hourRanges: Seq[String] =
    (0 until 24)
      .map(_.toString.reverse.padTo(2, "0").reverse.mkString)
      .map(_ ++ "0000")

  private val UrlBase = "https://dumps.wikimedia.org/other/pageviews/"

  private def getDayURLs(day: String): Seq[String] =
    hourRanges.map(hour =>
      UrlBase ++ "2020/2020-01/pageviews-" ++ day ++ "-" ++ hour ++ ".gz"
    )

  def run(args: List[String]): IO[ExitCode] = {
    List("20200101")
      .flatMap(getDayURLs)
      .map(url => {
        val filename = stringHash(url).toString ++ ".gz"
        (FileUtils.downloadFile(url, filename) *> FileUtils.openGZIPFile(filename))
          .map(iter => getNMost(iter.map(parseLine), 5))
      })
      .parSequence
      .map(_ foldMap identity)
      .map(count => {
        println(count)
        count
      })
      .as(ExitCode.Success)
  }
}
