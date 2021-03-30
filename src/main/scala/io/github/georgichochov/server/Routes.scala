package io.github.georgichochov.server

import akka.http.scaladsl.coding.Coders
import akka.http.scaladsl.marshallers.sprayjson.SprayJsonSupport
import akka.http.scaladsl.model.{ContentTypes, HttpEntity, StatusCodes}
import akka.http.scaladsl.server.Directives.{
  as,
  complete,
  decodeRequest,
  entity,
  get,
  onComplete,
  path,
  post
}
import akka.http.scaladsl.server.{Directives, Route}
import io.github.georgichochov.core.BennyNamePermutator
import io.github.georgichochov.models.{LastName, Title}
import io.github.georgichochov.persistence.repositories.{
  LastNamesRepository,
  TitlesRepository
}
import spray.json.DefaultJsonProtocol

import scala.concurrent.ExecutionContext
import scala.util.{Failure, Random, Success}

class Routes(
    lastNamesRepository: LastNamesRepository,
    titlesRepository: TitlesRepository
)(implicit ec: ExecutionContext)
    extends SprayJsonSupport
    with DefaultJsonProtocol {

  import io.github.georgichochov.bennycore.BennyName._

  val routes: Route = Directives.concat(
    path("bennies") {
      get {
        val eventualBennyNames = for {
          titles <- titlesRepository.fetchAll
          lastNames <- lastNamesRepository.fetchAll
        } yield {
          BennyNamePermutator.permutate(titles, lastNames)
        }
        onComplete(eventualBennyNames) {
          case Failure(exception) =>
            exception.printStackTrace()
            complete(StatusCodes.InternalServerError -> "Something went wrong")
          case Success(bennies) =>
            complete(bennies)
        }
      }
    },
    path("last-names") {
      post {
        decodeRequest {
          entity(as[String]) { name =>
            lastNamesRepository.addOne(new LastName() {
              override def lastName: String = name
            })
            complete(HttpEntity(name))
          }
        }
      }
    },
    path("titles") {
      post {
        decodeRequest {
          entity(as[String]) { newTitle =>
            titlesRepository.addOne(new Title() {
              override def title: String = newTitle
            })
            complete(HttpEntity(newTitle))
          }
        }
      }
    }
  )
}
