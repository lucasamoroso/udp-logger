import sbt._

object Dependencies {

  object ZIO {
    val zioVersion     = "1.0.0-RC21"
    val loggingVersion = "0.3.2"

    val zio          = "dev.zio" %% "zio"               % zioVersion
    val macros       = "dev.zio" %% "zio-macros"        % zioVersion
    val logging      = "dev.zio" %% "zio-logging"       % loggingVersion
    val loggingSlf4j = "dev.zio" %% "zio-logging-slf4j" % loggingVersion

    val all: Seq[ModuleID] = Seq(zio, macros, logging, loggingSlf4j)
  }

  object Config {
    private val version  = "0.12.3"
    private val refinedV = "0.9.14"

    val pureConfig        = "com.github.pureconfig" %% "pureconfig"         % version
    val pureConfigRefined = "eu.timepit"            %% "refined-pureconfig" % refinedV

    val all: Seq[ModuleID] = Seq(pureConfig, pureConfigRefined)

  }

  object Logging {
    private val logbackV = "1.2.3"

    val Core    = "ch.qos.logback" % "logback-core"    % logbackV
    val Logback = "ch.qos.logback" % "logback-classic" % logbackV

    val all: Seq[ModuleID] = Seq(Core, Logback)
  }

}
