import org.scalatest.flatspec.AnyFlatSpec
import java.time.LocalDateTime
import wikipipeline.WikiStat
import wikipipeline.bridges.HTTPSourceBridge

class HTTPSourceBridgeSpec extends AnyFlatSpec {

  behavior of "the HTTPSourceBridge"

  class FakeBridge extends HTTPSourceBridge {
    def getChunkURLProxy = this.getChunkURL(_)
    val parseWikiStatsProxy = this.parseWikiStats
    val makeLocalPathProxy = this.makeLocalPath
  }
  val bridge = new FakeBridge

  behavior of "the getChunkURL method"

  it should "get a proper URL for a morning hour chunk" in {

    val output = bridge.getChunkURLProxy(LocalDateTime.parse("2019-01-01T06:00"))
    val expectedOutput =
      "https://dumps.wikimedia.org/other/pageviews/2019/2019-01/pageviews-20190101-060000.gz"
    assert(output === expectedOutput)
  }

  it should "get a proper URL for an evening hour chunk" in {

    val output = bridge.getChunkURLProxy(LocalDateTime.parse("2019-01-01T18:00"))
    val expectedOutput =
      "https://dumps.wikimedia.org/other/pageviews/2019/2019-01/pageviews-20190101-180000.gz"
    assert(output === expectedOutput)
  }

  behavior of "the parseWikiStats method"

  it should "map raw strings to WikiStat items" in {

    val data = Seq(
      "us Dunder_Mifflin 80 3",
      "fr Paris 31 0",
    ).iterator
    val output = bridge.parseWikiStatsProxy(data)

    val expectedOutput = Seq(
      WikiStat("us", "Dunder_Mifflin", 80),
      WikiStat("fr", "Paris", 31),
    )
    assert(output.toSeq === expectedOutput)
  }

  behavior of "the makeLocalPath method"

  it should "use the filename from the given URL as the local filename" in {

    val url =
      "https://dumps.wikimedia.org/other/pageviews/2019/2019-01/pageviews-20190101-180000.gz"
    val output = bridge.makeLocalPathProxy(url)
    assert(output.split("/").last === url.split("/").last)
  }
}
