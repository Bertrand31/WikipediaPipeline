package utils

import scala.util.chaining.scalaUtilChainingOps
import scala.collection.mutable.PriorityQueue

object IteratorUtils {

  implicit class ImprovedIterator[A](val iter: Iterator[A]) {

    def getNMostWithout(n: Int, getValue: A => Int, isRejected: A => Boolean)
                       (implicit ord: Ordering[A]): Seq[A] = {
      val baseHeap =
        PriorityQueue
          .empty(ord)
          .addAll(iter take n)
       val topN =
        if (!iter.hasNext) baseHeap
        else
          iter.foldLeft(baseHeap)((heap, line) => {
            // We only check the blacklist if we're about to queue an item: it saves us many lookups
            if (getValue(line) <= getValue(heap.head) || isRejected(line)) heap
            else
              heap.tap(_.dequeue).tap(_.enqueue(line))
          })
        topN.dequeueAll
    }
  }

}
