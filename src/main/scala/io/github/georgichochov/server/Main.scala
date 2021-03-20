package io.github.georgichochov.server

import akka.Done
import akka.actor.typed.ActorSystem
import akka.actor.typed.scaladsl.Behaviors
import akka.http.scaladsl.Http

import scala.concurrent.{Await, ExecutionContext, Future, Promise, blocking}
import scala.concurrent.duration.Duration
import scala.io.StdIn

object Main {

  def main(args: Array[String]): Unit = {
    implicit val system = ActorSystem(Behaviors.empty, "benny-system")
    implicit val ec: ExecutionContext = system.executionContext

    val getNewBenny = Routes.routes

    println("Hello from Main")
    val port = sys.env.getOrElse("PORT", "8080").toInt
    val binding = Http().newServerAt("0.0.0.0", port).bind(getNewBenny)
    Await.ready(
      binding.flatMap(_ =>
        waitForShutdownSignal()
      ), // chaining both futures to fail fast
      Duration.Inf
    )

    binding.flatMap(_.unbind()).onComplete { _ =>
      println("So long")
      system.terminate()
    }
  }
  protected def waitForShutdownSignal(
  )(implicit ec: ExecutionContext): Future[Done] = {
    val promise = Promise[Done]()
    sys.addShutdownHook {
      promise.trySuccess(Done)
    }
    Future {
      blocking {
        if (StdIn.readLine("Press RETURN to stop...\n") != null)
          promise.trySuccess(Done)
      }
    }
    promise.future
  }
}
