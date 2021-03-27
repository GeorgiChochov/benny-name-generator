package io.github.georgichochov.model

import scala.concurrent.{Await, Future}
import scala.concurrent.duration.Duration
import slick.jdbc.PostgresProfile.api._

import java.sql.Date
import java.util.UUID
import scala.reflect.ClassTag

class Titles(tag: Tag) extends Table[(UUID, String)](tag, "TITLES") {
  def id = column[UUID]("ID", O.PrimaryKey)
  def title = column[String]("TITLE")
  def * = (id, title)
}
