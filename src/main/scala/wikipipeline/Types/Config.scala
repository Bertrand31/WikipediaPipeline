package wikipipeline.types

case class Config(
  blacklistPath: String = "src/main/resources/data/",
  destinationPath: String = "",
  workingDirectory: String = "/tmp/",
)
