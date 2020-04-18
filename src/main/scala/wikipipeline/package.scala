import pureconfig.ConfigSource
import pureconfig.generic.auto._
import wikipipeline.types.Config

package object wikipipeline {

  // Load the app configuration file, cast it into a Config type.
  // If that fails, initialize a Config object with default values.
  lazy val AppConfig = ConfigSource.default.load[Config].getOrElse(Config())

  type WikiStat = (String, Int)

  object WikiStatOrdering extends Ordering[WikiStat] {

    def compare(a: WikiStat, b: WikiStat) = b._2 compare a._2
  }
}
