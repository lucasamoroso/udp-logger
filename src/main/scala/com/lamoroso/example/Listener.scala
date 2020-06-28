package com.lamoroso.example

import java.net.{ DatagramPacket, DatagramSocket }

import zio.clock.Clock
import zio.{ Fiber, Queue, RIO, UIO, URIO, ZIO, ZLayer }
import zio.logging.{ log, Logging }

object Listener {
  val live = ZLayer.succeed({
    new Service {

      override def run(
        queue: Queue[String],
        socket: DatagramSocket
      ): URIO[Logging with Clock, Fiber.Runtime[Throwable, Nothing]] = {
        val loop = for {
          buf     <- UIO(new Array[Byte](256))
          packet  <- UIO(new DatagramPacket(buf, buf.length))
          _       <- ZIO.effect(socket.receive(packet))
          address <- UIO(packet.getAddress)
          port    <- UIO(packet.getPort)
          _       <- log.debug(s"Received package from $address:$port")
          packet  <- UIO(new DatagramPacket(buf, buf.length, address, port))
          msg      = new String(packet.getData, 0, packet.getLength)
          _       <- queue.offer(msg)
          size    <- queue.size
          _       <- log.info(s"Queue size $size")
        } yield ()
        loop.forever.fork
      }
    }
  })

  def run(
    queue: Queue[String],
    socket: DatagramSocket
  ): URIO[Listener with Clock with Logging, Fiber.Runtime[Throwable, Nothing]] =
    RIO.accessM(_.get.run(queue, socket))

  trait Service {
    def run(
      queue: Queue[String],
      socket: DatagramSocket
    ): URIO[Clock with Logging, Fiber.Runtime[Throwable, Nothing]]
  }

}
