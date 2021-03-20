package io.github.georgichochov.server

import akka.http.scaladsl.model.{ContentTypes, HttpEntity}
import akka.http.scaladsl.server.Directives.{
  as,
  complete,
  decodeRequest,
  entity,
  get,
  path,
  post
}
import akka.http.scaladsl.server.{Directives, Route}

import scala.collection.mutable
import scala.util.Random

object Routes {

  private val defaultName = "Popplewell"
  private val names = mutable.Set(defaultName)
  private val defaultTitle = "Sir"
  private val titles = mutable.Set(defaultTitle)
  private val nameDynasty = mutable.Map(defaultName -> 1)

  val routes: Route = Directives.concat(
    path("benny") {
      get {
        complete {
          val titleIndex = Random.nextInt(titles.size)
          val title = titles.view
            .slice(titleIndex, titleIndex + 1)
            .headOption
            .getOrElse(defaultTitle)
          val nameIndex = Random.nextInt(names.size)
          val fancyName = names.view.slice(nameIndex, nameIndex + 1).head
          val numeral = nameDynasty(fancyName)
          nameDynasty.put(fancyName, numeral + 1)

          HttpEntity(
            ContentTypes.`application/json`,
            s"""{
             |  "title": "$title",
             |  "fancyName": "$fancyName",
             |  "numeral": $numeral
             |}""".stripMargin
          )
        }
      }
    },
    path("name") {
      post {
        decodeRequest {
          entity(as[String]) { name =>
            names += name
            nameDynasty.put(name, 1)
            complete(HttpEntity(name))
          }
        }
      }
    },
    path("title") {
      post {
        decodeRequest {
          entity(as[String]) { title =>
            titles += title
            complete(HttpEntity(title))
          }
        }
      }
    }
  )
}
