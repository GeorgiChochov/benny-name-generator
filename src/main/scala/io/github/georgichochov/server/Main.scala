package io.github.georgichochov.server

import akka.actor.typed.ActorSystem
import akka.actor.typed.scaladsl.Behaviors
import akka.http.scaladsl.Http

import scala.concurrent.ExecutionContext
import scala.io.StdIn

object Main {

  def main(args: Array[String]): Unit = {
    implicit val system = ActorSystem(Behaviors.empty, "benny-system")
    implicit val ec: ExecutionContext = system.executionContext

    val getNewBenny = Routes.routes

    val binding = Http().newServerAt("localhost", 8080).bind(getNewBenny)
    StdIn.readLine()
    binding.flatMap(_.unbind()).onComplete(_ => system.terminate())
  }

}
