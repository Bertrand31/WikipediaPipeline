package utils

import scala.util.Try
import java.time.LocalDateTime

object DateUtils {

  def getHoursBetween(
    start: LocalDateTime, end: LocalDateTime, soFar: List[LocalDateTime] = List.empty,
  ): List[LocalDateTime] =
    if (start.isAfter(end)) soFar.reverse
    else getHoursBetween(start.plusHours(1), end, start +: soFar)

  def parseDate(str: String): Try[LocalDateTime] =
    Try { LocalDateTime.parse(str) }
}
