import org.scalatest.flatspec.AnyFlatSpec
import airportmatching._
import airportmatching.Artemis

class ArtemisSpec extends AnyFlatSpec {

  behavior of "the Artemis tree"

  val data: List[Airport] = List(
    Airport("foo", Point(1f, 3f)),
    Airport("bar", Point(3f, 2f)),
    Airport("baz", Point(3f, 1f)),
    Airport("test", Point(8f, 9f)),
    Airport("blop", Point(8f, -2f)),
    Airport("mot", Point(-5f, 90f)),
    Airport("wat", Point(0f, 0f)),
    Airport("airport", Point(-34f, -4f)),
    Airport("cdg", Point(8f, 88f)),
  )

  it should "return the nearest element correctly" in {

    val tree = Artemis(data)

    assert(tree.nearest(Point(3f, 3f)) === Airport("bar", Point(3f, 2f)))
    assert(tree.nearest(Point(-3f, -5f)) === Airport("wat", Point(0f, 0f)))
    assert(tree.nearest(Point(-30f, -5f)) === Airport("airport", Point(-34f, -4f)))
  }

  val airports: List[Airport] = List(
    Airport("FEL", Point(48.2056f, 11.26614f)),
    Airport("GPA", Point(38.15111f, 21.425556f)),
    Airport("KSD", Point(59.44472f, 13.3375f)),
    Airport("NRK", Point(58.58597f, 16.24054f)),
    Airport("YGK", Point(44.22528f, -76.59694f)),
    Airport("BVD", Point(-7.083f, 29.73f)),
  )

  it should "return the nearest airport" in {

    val tree = Artemis(airports)
    val user1 = Point(44.9748f, 5.0264f)
    assert(tree.nearest(user1).name === "FEL")

    val user2 = Point(51.9292f,4.5778f)
    assert(tree.nearest(user2).name === "FEL")

    val user3 = Point(59.9127f, 10.7461f)
    assert(tree.nearest(user3).name === "KSD")
  }
}
