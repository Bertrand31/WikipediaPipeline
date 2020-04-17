package wikipipeline

import utils.FileUtils

object BlacklistHandler {

  private val BlacklistPath = "src/main/resources/data/blacklist_domains_and_pages"

  private val Blacklist =
    FileUtils.openFile(BlacklistPath)
      .map(_.toSet)
      .unsafeRunSync

  def isBlacklisted(wikiStat: WikiStat): Boolean =
    Blacklist.contains(wikiStat._1)
}
