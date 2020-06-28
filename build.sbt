import Dependencies._
import sbt._
import sbt.Package.ManifestAttributes

lazy val udpLogger = project
  .in(file("."))
  .settings(thisBuildSettings)
  .settings(Compile / mainClass := Some("com.lamoroso.example.Main"))
  .settings(alias)

lazy val thisBuildSettings = inThisBuild(
  Seq(
    name := "udp-logger",
    version := "0.1",
    scalaVersion := "2.13.2",
    description := "An udp logger",
    libraryDependencies ++= dependencies ++ plugins,
    scalacOptions ++= List(
      "-Yrangepos",
      "-P:semanticdb:synthetics:on"
    )
  )
)
lazy val dependencies      = ZIO.all ++ Config.all ++ Logging.all

lazy val plugins = Seq(
  compilerPlugin("org.typelevel" %% "kind-projector"     % "0.11.0" cross CrossVersion.full),
  compilerPlugin("io.tryp"        % "splain"             % "0.5.6" cross CrossVersion.patch),
  compilerPlugin("com.olegpy"    %% "better-monadic-for" % "0.3.1"),
  compilerPlugin(scalafixSemanticdb)
)

val alias: Seq[sbt.Def.Setting[_]] =
  addCommandAlias("build", "prepare; testJVM") ++ addCommandAlias("prepare", "fix; fmt") ++ addCommandAlias(
    "fix",
    "all compile:scalafix test:scalafix"
  ) ++ addCommandAlias("fixCheck", "; compile:scalafix --check ; test:scalafix --check") ++ addCommandAlias(
    "fmt",
    "all root/scalafmtSbt root/scalafmtAll"
  ) ++ addCommandAlias("fmtCheck", "all root/scalafmtSbtCheck root/scalafmtCheckAll")

scalafixDependencies in ThisBuild += "com.nequissimus" %% "sort-imports" % "0.5.0"
