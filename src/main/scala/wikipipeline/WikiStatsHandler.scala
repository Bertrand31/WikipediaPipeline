package wikipipeline

import scala.util.chaining.scalaUtilChainingOps
import scala.collection.mutable.PriorityQueue
import cats.effect.IO

object WikiStatHandler {

  def getNMostWithout(n: Int, isRejected: WikiStat => IO[Boolean])
                     (iter: Iterator[WikiStat]): IO[Map[String, Int]] = {
    val baseHeap =
      IO.pure {
        PriorityQueue
          .empty(WikiStatOrdering)
          .addAll(iter take n)
      }
     val ioTopN =
      if (!iter.hasNext) baseHeap
      else
        iter.foldLeft(baseHeap)((ioHeap, line) => {
          // We only check the blacklist if we're about to queue an item: it saves us many lookups
          val (_, count) = line
          ioHeap.flatMap(heap => {
            if (count <= heap.head._2) IO.pure(heap)
            else
              isRejected(line) map {
                case false => heap.tap(_.dequeue).tap(_.enqueue(line))
                case _     => heap
              }
          })
        })
      ioTopN.map(_.dequeueAll.toMap)
  }
}
