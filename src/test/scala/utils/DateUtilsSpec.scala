import org.scalatest.flatspec.AnyFlatSpec
import utils.DateUtils
import java.time.LocalDateTime

class DateUtilsSpec extends AnyFlatSpec {

  behavior of "the date utils"

  behavior of "the getHoursBetween function"

  it should "return all the dates with 1 hour interval between two dates" in {

    val start = LocalDateTime.parse("2018-01-01T23:00")
    val end = LocalDateTime.parse("2018-01-02T02:34")

    val expectedOutput = List(
      "2018-01-01T23:00",
      "2018-01-02T00:00",
      "2018-01-02T01:00",
      "2018-01-02T02:00",
    ).map(LocalDateTime.parse)

    val output = DateUtils.getHoursBetween(start, end)
    assert(output === expectedOutput)
  }
}
