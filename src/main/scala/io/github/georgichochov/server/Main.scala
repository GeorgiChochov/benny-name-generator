package io.github.georgichochov.server

import akka.Done
import akka.actor.typed.ActorSystem
import akka.actor.typed.scaladsl.Behaviors
import akka.http.scaladsl.Http
import io.github.georgichochov.model.Titles
import slick.dbio.DBIO
import slick.jdbc.JdbcBackend.Database
import slick.lifted.TableQuery
import slick.jdbc.PostgresProfile.api._

import java.net.URI
import java.util.UUID
import scala.concurrent.{Await, ExecutionContext, Future, Promise, blocking}
import scala.concurrent.duration.Duration
import scala.io.StdIn

object Main {

  def main(args: Array[String]): Unit = {
    implicit val system = ActorSystem(Behaviors.empty, "benny-system")
    implicit val ec: ExecutionContext = system.executionContext

    val getNewBenny = Routes.routes

    val dbUrl =
      sys.env.getOrElse(
        "DATABASE_URL",
        "postgres://test:test@127.0.0.1:5432/test"
      )
    println(s"db url: $dbUrl")
    val dbUri = new URI(dbUrl)
    val username = dbUri.getUserInfo.split(":").head
    val passwd = dbUri.getUserInfo.split(":").last
    val connStr =
      s"jdbc:postgresql://${dbUri.getHost}:${dbUri.getPort}${dbUri.getPath}"

    val db = Database.forURL(
      connStr,
      user = username,
      password = passwd,
      driver = "org.postgresql.Driver"
    )

    val titles = TableQuery[Titles]

    try {
      val setupFuture = db.run(
        DBIO.seq(
          titles.schema.createIfNotExists,
          titles += (UUID.randomUUID(), "Mr.")
        )
      )

      Await.result(setupFuture, Duration.Inf)
      println(
        Await.result(
          db.run(titles.filter(_.title === "Mr.").result),
          Duration.Inf
        )
      )
    } catch {
      case e => e.printStackTrace()
    }
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
