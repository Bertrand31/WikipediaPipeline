import org.scalatest.flatspec.AnyFlatSpec
import airportmatching._
import utils.GeoUtils

class GeoUtilsSpec extends AnyFlatSpec {

  behavior of "the distance function"

  it should "return the correct value using the Haversine formula" in {

    {
      val point1 = Point(-90, 4)
      val point2 = Point(5.1291f, 179.1224f)
      assert(GeoUtils.distance(point1, point2) === 10577.873279126225)
    }

    {
      val point1 = Point(67.321890f, -170.234f)
      val point2 = Point(-85.1291f, 1.49012f)
      assert(GeoUtils.distance(point1, point2) === 18027.925955941926)
    }
  }
}
