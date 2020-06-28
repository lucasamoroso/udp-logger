package com.lamoroso.example

import java.io.File

import zio.logging.log
import java.net.DatagramSocket

import com.lamoroso.example.config.Configuration
import com.lamoroso.example.logging.Logger
import pureconfig.ConfigSource
import zio.{ App, ExitCode, Queue, Task, UIO, URIO, ZIO }
import pureconfig.generic.auto._
import zio.clock.Clock

object Main extends App {

  val logger   = Logger.live
  val writer   = (Logger.live ++ Clock.live) >>> Writer.live
  val listener = (Logger.live ++ Clock.live) >>> Listener.live
  val appLayer = Logger.live ++ writer ++ listener

  override def run(args: List[String]): URIO[zio.ZEnv, ExitCode] =
    program.provideCustomLayer(appLayer).exitCode

  val program = for {
    config <- Task.effect(ConfigSource.default.at("udp-logger").loadOrThrow[Configuration])
    queue  <- Queue.bounded[String](config.queue.capacity)
    _      <- ZIO.effect(new DatagramSocket(config.listenPort)).bracket(s => UIO(s.close())) { socket =>
                for {
                  _         <- log.info(s"Listening on ${config.listenPort}")
                  file      <- Task.effect(new File(config.outputDir))
                  listenerF <- Listener.run(queue, socket)
                  _         <- log.debug(s"Listener ${listenerF.id.toString}")
                  writerF   <- Writer.run(queue, config.queue.takeUpTo, file)
                  _         <- log.debug(s"Writer ${writerF.id.toString}")
                  _         <- listenerF.join
                } yield ()
              }
  } yield ()
}
