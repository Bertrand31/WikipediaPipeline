package airportmatching

import cats.effect._
import cats.implicits._
import utils.FileUtils
import scala.collection.mutable.PriorityQueue
import scala.collection.immutable.Queue

object Main extends IOApp {

  private def parseLine(line: String): (String, Int) = {
    val Array(domain, title, count, _) = line.split(" ")
    (domain + "|" + title, count.toInt)
  }

  private def getNMost(iter: Iterator[(String, Int)], n: Int): Map[String, Int] = {
    val initial = iter.take(n).toArray.sortBy(-_._2)
    val heap = PriorityQueue.empty(Ordering[Int].reverse).addAll(initial.map(_._2))
    val initialQueue = Queue().enqueueAll(initial.map(_._1))
    val mostViewed = iter.foldLeft(initialQueue)((queue, line) => {
      val (page, count) = line
      if (count <= heap.head) queue
      else {
        heap.dequeue()
        heap.enqueue(count)
        queue
          .dequeue
          ._2
          .enqueue(page)
      }
    })
    (mostViewed zip heap).toMap
  }

  def run(args: List[String]): IO[ExitCode] = {
    val ur = "https://dumps.wikimedia.org/other/pageviews/2020/2020-01/pageviews-20200101-000000.gz"
    val filename = "test.txt.gz"
    (FileUtils.downloadFile(ur, filename) *> FileUtils.openGZIPFile(filename))
      .flatMap(iter => {
        val mostViewed = getNMost(iter.map(parseLine), 5)
        IO { println(mostViewed.toList) }
      })
      .as(ExitCode.Success)
  }
}
