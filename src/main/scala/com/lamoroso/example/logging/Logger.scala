package com.lamoroso.example.logging

import zio.logging.slf4j.Slf4jLogger
import zio.logging.Logging
import zio.ULayer

object Logger {
  val live: ULayer[Logging] = Slf4jLogger.make { (_, message) =>
    logFormat.format(message)
  }
  private val logFormat     = "%s"

}
