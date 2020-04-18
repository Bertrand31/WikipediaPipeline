package utils

import scala.util.chaining.scalaUtilChainingOps
import scala.collection.mutable.PriorityQueue

object IteratorUtils {

  implicit class ImprovedIterator[A](val iter: Iterator[A]) {

    /** While this method is very specific to our business logic needs, it still is generic enough
      * that it can be made a method of Iterator. After all, we're not building a public library but
      * instead, a service performing a very specific task.
      */
    def getNMostByWithout[T](n: Int, groupBy: A => T, isRejected: A => Boolean)
                         (implicit ord: Ordering[A]): Map[T, Seq[A]] =
        iter.foldLeft(Map[T, PriorityQueue[A]]())((map, item) => {
          val key = groupBy(item)
          val pQueue = map.getOrElse(key, PriorityQueue.empty(ord))
          if (pQueue.size >= n && ord.gt(item, pQueue.head)) map
          else if (isRejected(item)) map // We only use the predicate if we're about to queue an item
          else {
            val base =
              if (pQueue.size < n) pQueue
              else pQueue.tap(_.dequeue)
            map.updated(key, base.tap(_.enqueue(item)))
          }
        })
          .view
          .mapValues(_.dequeueAll)
          .toMap
  }

}
