import org.scalatest.flatspec.AnyFlatSpec
import scala.collection.immutable.ArraySeq
import utils.IteratorUtils._

class IteratorUtilsSpec extends AnyFlatSpec {

  behavior of "the +: method on iterators"

  it should "prepend a value to an iterator" in {

    val iter = Iterator(1, 2, 3)
    val iterWith0 = 0 +: iter
    val expectedOutput = Iterator(0, 1, 2, 3)
    assert(iterWith0.toList === expectedOutput.toList)
  }

  behavior of "the getNMostByWithout iterator method"

  it should "get the first 3 values, grouped by whether they are even, minus the multiples of 3" in {

    val data = Seq(1, 2, 3, 4, 5, 6, 7, 8, 9, 10).iterator

    val map = data.groupNMostWithout(3, nb => nb % 2 == 0, nb => nb % 3 == 0)
    val expectedMap = Map(
      (false -> ArraySeq(7, 5, 1)),
      (true -> ArraySeq(8, 4, 2)),
    )
    assert(map == expectedMap)
  }

  it should "return values in the same order even if they're reversed" in {

    val data = Seq(1, 2, 3, 4, 5, 6, 7, 8, 9, 10).reverse.iterator

    val map = data.groupNMostWithout(3, nb => nb % 2 == 0, nb => nb % 3 == 0)
    val expectedMap = Map(
      (false -> ArraySeq(7, 5, 1)),
      (true -> ArraySeq(8, 4, 2)),
    )
    assert(map == expectedMap)
  }

  it should "return values in the reverse order if given a reverse Ordering implicit" in {

    val data = Seq(1, 2, 3, 4, 5, 6, 7, 8, 9, 10).iterator

    val map = data.groupNMostWithout(3, nb => nb % 2 == 0, nb => nb % 3 == 0)(Ordering[Int].reverse)
    val expectedMap = Map(
      (false -> ArraySeq(1, 5, 7)),
      (true -> ArraySeq(4, 8, 10)),
    )
    assert(map == expectedMap)
  }

  it should "get the first 2 values by order, grouped by length, minus blacklisted values" in {

    val blacklist = Set("shit", "poop")

    val data = Seq("foo", "bar", "baz", "shit", "test", "poop").iterator

    val map = data.groupNMostWithout(2, _.size, blacklist.contains)
    val expectedMap = Map(
      (3 -> ArraySeq("baz", "bar")),
      (4 -> ArraySeq("test")),
    )
    assert(map == expectedMap)
  }
}
