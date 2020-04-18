package wikipipeline.types

case class Config(
  blacklistPath: String = "src/main/resources/data/blacklist_domains_and_pages",
  destinationPath: String = "",
  workingDirectory: String = "/tmp/",
)
