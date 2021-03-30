package io.github.georgichochov.persistence.jdbc.postgresql.tables

import io.github.georgichochov.persistence.jdbc.postgresql.models.TitleJdbc
import slick.jdbc.PostgresProfile.api._

import java.util.UUID

class Titles(tag: Tag) extends Table[TitleJdbc](tag, "titles") {
  def id = column[UUID]("id", O.PrimaryKey)
  def title = column[String]("title", O.Unique)
  def * = (id, title).mapTo[TitleJdbc]
}
