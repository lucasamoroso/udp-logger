package com.lamoroso.example

import java.io.{ File, PrintWriter }

import zio.clock.Clock
import zio.logging.{ log, Logging }
import zio.{ Fiber, Queue, RIO, Task, UIO, ULayer, URIO, ZIO, ZLayer }
import zio.duration._

object Writer {

  val live: ULayer[Writer] = ZLayer.succeed({
    new Service {
      override def run(
        queue: Queue[String],
        takeUpTo: Int,
        file: File
      ): URIO[Clock with Logging, Fiber.Runtime[Throwable, Nothing]] = {
        val loop = for {
          _        <- ZIO.sleep(10.seconds)
          messages <- queue.takeUpTo(takeUpTo)
          _        <- if (messages.nonEmpty) log.debug(s"${messages.size} lines to log") else ZIO.none
          _        <- Task(new PrintWriter(file)).bracket(close) { pw =>
                        ZIO.foreach(messages)(msg => Task.effect(pw.println(msg)))
                      }

        } yield ()
        loop.forever.fork
      }

      private def close(pw: PrintWriter): URIO[Logging, Unit] =
        for {
          _ <- log.debug("Closing file")
          _ <- UIO(pw.close())
        } yield ()
    }
  })

  //Accessors
  def run(
    queue: Queue[String],
    takeUpTo: Int,
    file: File
  ): URIO[Writer with Clock with Logging, Fiber.Runtime[Throwable, Nothing]] =
    RIO.accessM(_.get.run(queue, takeUpTo, file))

  trait Service {
    def run(
      queue: Queue[String],
      takeUpTo: Int,
      file: File
    ): URIO[Clock with Logging, Fiber.Runtime[Throwable, Nothing]]
  }

}
