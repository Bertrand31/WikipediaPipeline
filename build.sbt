name := "WikipediaPageviewPipeline"

version := "0.1"

scalaVersion := "2.13.1"

libraryDependencies ++= Seq(
  "io.estatico" %% "newtype" % "0.4.3",
  "org.typelevel" %% "cats-core" % "2.1.1",
  "org.typelevel" %% "cats-effect" % "2.1.2",
  "org.scalatest" %% "scalatest" % "3.1.0",
  // HTTP client
  "com.softwaremill.sttp.client" %% "core" % "2.0.7",
  "com.softwaremill.sttp.client" %% "async-http-client-backend-cats" % "2.0.7",
)

scalacOptions ++= Seq(
  "-Ymacro-annotations", // Needed by newtype
  "-deprecation", // Warn about deprecated features
  "-encoding", "UTF-8", // Specify character encoding used by source files
  "-feature", // Emit warning and location for usages of features that should be imported explicitly
  "-language:existentials", // Existential types (besides wildcard types) can be written and inferred
  "-language:higherKinds", // Allow higher-kinded types
  "-unchecked", // Enable additional warnings where generated code depends on assumptions
  "-Xlint:_", // Enable all available style warnings
  "-Ywarn-macros:after", // Only inspect expanded trees when generating unused symbol warnings
  "-Ywarn-unused:_", // Enables all unused warnings
  "-Ywarn-value-discard", // Warn when non-Unit expression results are unused
)

scalacOptions in Test --= Seq(
  "-Xlint:_",
  "-Ywarn-unused-import",
)

javaOptions ++= Seq(
  "-XX:+CMSClassUnloadingEnabled", // Enable class unloading under the CMS GC
  "-Xms2g",
  "-Xmx12g",
)

enablePlugins(JavaServerAppPackaging)
