import org.scalatest.flatspec.AnyFlatSpec
import wikipipeline.WikiStat
import wikipipeline.bridges.FileDestinationBridge

class FileDestinationBridgeSpec extends AnyFlatSpec {

  behavior of "the FileDestinationBridge"

  behavior of "the statsToCSVLines method"

  class FakeBridge extends FileDestinationBridge {
    def statsToCSVLinesProxy = this.statsToCSVLines
  }

  it should "turn the map of stats to rows of CSV data" in {

    val fakeData = Map[String, Seq[WikiStat]](
      ("fr" -> Seq(WikiStat("fr", "Main_Page", 900))),
      ("sg" -> Seq(WikiStat("sg", "Kopi_Peng", 100), WikiStat("sg", "Bak Kut Teh", 300))),
    )

    val output = new FakeBridge().statsToCSVLinesProxy(fakeData)
    val expectedOutput = Seq(
      "domain;page;views",
      "fr;Main_Page;900",
      "sg;Kopi_Peng;100",
      "sg;Bak Kut Teh;300",
    )
    assert(output.toList === expectedOutput)
  }
}
