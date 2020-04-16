package wikipipeline

import scala.util.chaining.scalaUtilChainingOps
import scala.collection.mutable.PriorityQueue

object WikiStatHandler {

  def getNMost(n: Int)(iter: Iterator[WikiStat]): Map[String, Int] = {
    val baseHeap =
      PriorityQueue
        .empty(WikiStatOrdering)
        .addAll(iter take n)
    val results =
      if (!iter.hasNext) baseHeap
      else
        iter.foldLeft(baseHeap)((heap, line) =>
          if (line._2 <= heap.head._2)
            heap
          else
            heap.tap(_.dequeue).tap(_.enqueue(line))
        )
    results.dequeueAll.toMap
  }
}
