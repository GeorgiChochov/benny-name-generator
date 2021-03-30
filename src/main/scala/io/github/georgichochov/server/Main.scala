package io.github.georgichochov.server

import akka.Done
import akka.actor.typed.ActorSystem
import akka.actor.typed.scaladsl.Behaviors
import akka.http.scaladsl.Http
import io.github.georgichochov.persistence.jdbc.postgresql.DbSetup
import io.github.georgichochov.persistence.jdbc.postgresql.repositories.{
  LastNamesJdbcRepository,
  TitlesJdbcRepository
}
import slick.dbio.DBIO
import slick.jdbc.PostgresProfile.api._

import scala.concurrent.duration.Duration
import scala.concurrent._
import scala.io.StdIn

object Main {

  def main(args: Array[String]): Unit = {
    implicit val system = ActorSystem(Behaviors.empty, "benny-system")
    implicit val ec: ExecutionContext = system.executionContext

    val dbUrl =
      sys.env.getOrElse(
        "DATABASE_URL",
        "postgres://test:test@127.0.0.1:5432/test"
      )
    val db = new DbSetup().setUp(dbUrl)
    val lastNamesRepository = new LastNamesJdbcRepository(db)
    val titlesRepository = new TitlesJdbcRepository(db)
    try {
      val setupFuture = db.run(
        DBIO.seq(
          titlesRepository.schema.createIfNotExists,
          lastNamesRepository.schema.createIfNotExists
        )
      )

      Await.result(setupFuture, Duration.Inf)
      println("Database ready")
    } catch {
      case e: Throwable => e.printStackTrace()
    }

    val routes = new Routes(lastNamesRepository, titlesRepository).routes

    println("Hello from Main")
    val port = sys.env.getOrElse("PORT", "8080").toInt
    val binding = Http().newServerAt("0.0.0.0", port).bind(routes)
    Await.ready(
      binding.flatMap(_ =>
        waitForShutdownSignal()
      ), // chaining both futures to fail fast
      Duration.Inf
    )

    binding.flatMap(_.unbind()).onComplete { _ =>
      println("So long")
      db.close()
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
