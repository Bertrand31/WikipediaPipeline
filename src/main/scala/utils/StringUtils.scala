package utils

import cats.implicits._

object StringUtils {

  implicit class StringImprovements(val s: String) {

    def padLeft(minLength: Int, padWith: Char): String =
      if (s.length >= minLength) s
      else padWith.toString * (minLength - s.length) |+| s
  }
}
