import org.scalatest.flatspec.AnyFlatSpec
import java.time.LocalDateTime
import utils.DateUtils

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

  it should "be inclusive of the last date if it falls exactly one the last hour" in {

    val start = LocalDateTime.parse("2018-01-01T23:34")
    val end = LocalDateTime.parse("2018-01-02T02:34")

    val expectedOutput = List(
      "2018-01-01T23:34",
      "2018-01-02T00:34",
      "2018-01-02T01:34",
      "2018-01-02T02:34",
    ).map(LocalDateTime.parse)

    val output = DateUtils.getHoursBetween(start, end)
    assert(output === expectedOutput)
  }
}
