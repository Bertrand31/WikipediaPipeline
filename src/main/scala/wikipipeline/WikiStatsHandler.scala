package wikipipeline

import scala.util.chaining.scalaUtilChainingOps
import scala.collection.mutable.PriorityQueue

object WikiStatHandler {

  def getNMostWithout(n: Int, predicate: WikiStat => Boolean)
                     (iter: Iterator[WikiStat]): Map[String, Int] = {
    val baseHeap =
      PriorityQueue
        .empty(WikiStatOrdering)
        .addAll(iter take n)
    val results =
      if (!iter.hasNext) baseHeap
      else
        iter.foldLeft(baseHeap)((heap, line) => {
          // We only check the blacklist if we're about to queue an item: it saves us many lookups
          val (_, count) = line
          if (count <= heap.head._2 || predicate(line))
            heap
          else
            heap.tap(_.dequeue).tap(_.enqueue(line))
        })
    results.dequeueAll.toMap
  }
}
