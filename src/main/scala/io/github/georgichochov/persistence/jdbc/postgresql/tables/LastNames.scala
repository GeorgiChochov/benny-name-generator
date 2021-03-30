package io.github.georgichochov.persistence.jdbc.postgresql.tables

import io.github.georgichochov.persistence.jdbc.postgresql.models.LastNameJdbc
import slick.jdbc.PostgresProfile.api._

import java.util.UUID

class LastNames(tag: Tag) extends Table[LastNameJdbc](tag, "last_names") {
  def id = column[UUID]("id", O.PrimaryKey)
  def lastName = column[String]("last_name", O.Unique)
  def * = (id, lastName).mapTo[LastNameJdbc]
}
