import org.scalatest.flatspec.AnyFlatSpec
import java.time.LocalDateTime
import cats.effect.IO
import wikipipeline.{IngestionHandler, WikiStat}
import wikipipeline.bridges.{DestinationBridge, SourceBridge}

class IngestionHandlerSpec extends AnyFlatSpec {

  behavior of "the ingestHourRange method"

  it should "call the source bridge with the date, and the destination bridge with an ID string" in {

    val fakeData = Map[String, Seq[WikiStat]](
      ("fr" -> Seq(WikiStat("fr", "Main_Page", 900))),
      ("sg" -> Seq(WikiStat("sg", "Main_Page", 100))),
    )

    object FakeSourceBridge extends SourceBridge {
      def getTopNForFile(n: Int)(time: LocalDateTime): IO[Map[String, Seq[WikiStat]]] = {
        IO.pure(fakeData)
      }
    }

    object FakeDestinationBridge extends DestinationBridge {
      def write(dayId: String)(results: => IO[Map[String, Seq[WikiStat]]]): IO[Unit] = {
        assert(dayId === "2019-01-01-10")
        assert(results.unsafeRunSync === fakeData)
        IO.pure(())
      }
    }

    val ingestionHandler = new IngestionHandler(FakeSourceBridge, FakeDestinationBridge)
    ingestionHandler.ingestHour(LocalDateTime.parse("2019-01-01T10:30"))
  }
}
