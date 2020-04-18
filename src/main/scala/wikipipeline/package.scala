import pureconfig.ConfigSource
import pureconfig.generic.auto._
import wikipipeline.types.Config

package object wikipipeline {

  // Load the app configuration file, cast it into a Config type
  lazy val AppConfig = ConfigSource.default.loadOrThrow[Config]

  type WikiStat = (String, Int)

  object WikiStatOrdering extends Ordering[WikiStat] {

    def compare(a: WikiStat, b: WikiStat) = b._2 compare a._2
  }
}
