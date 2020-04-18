import scala.language.implicitConversions
import io.estatico.newtype.ops.toCoercibleIdOps
import io.estatico.newtype.macros.newtype
import pureconfig.ConfigSource
import pureconfig.generic.auto._
import wikipipeline.types.Config

package object wikipipeline {

  // Load the app configuration file, cast it into a Config type.
  // If that fails, initialize a Config object with default values.
  lazy val AppConfig =
    ConfigSource
      .default
      .load[Config]
      .getOrElse(Config())

  @newtype case class WikiStat(v: (String, String, Int)) {

    // Since during runtime WikiStat is no more than a truple, we need to define getters
    // manually t make it as convenient as a normal case class.
    def domain: String = v._1
    def page: String   = v._2
    def views: Int     = v._3
  }

  object WikiStat {

    def apply(domain: String, page: String, views: Int): WikiStat =
      (domain, page, views).coerce[WikiStat]

    def unapply(stat: WikiStat): Option[(String, String, Int)] =
      Some(stat.v)
  }

  object WikiStatOrdering extends Ordering[WikiStat] {

    def compare(a: WikiStat, b: WikiStat) = b.views compare a.views
  }
}
