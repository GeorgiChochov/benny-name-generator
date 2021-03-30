package io.github.georgichochov.persistence.jdbc.postgresql.repositories

import io.github.georgichochov.models.LastName
import io.github.georgichochov.persistence.jdbc.postgresql.models.LastNameJdbc
import io.github.georgichochov.persistence.jdbc.postgresql.tables.LastNames
import io.github.georgichochov.persistence.repositories.LastNamesRepository
import slick.jdbc.JdbcBackend
import slick.jdbc.PostgresProfile.api._
import slick.lifted.TableQuery

import java.util.UUID
import scala.concurrent.{ExecutionContext, Future}

class LastNamesJdbcRepository(db: JdbcBackend.DatabaseDef)(implicit
    ec: ExecutionContext
) extends LastNamesRepository {

  private val tableQuery = TableQuery[LastNames]
  val schema = tableQuery.schema

  override def fetchAll: Future[Set[LastName]] = {
    db.run(tableQuery.result).map(_.toSet)
  }

  override def fetch100: Future[Set[LastName]] = {
    db.run(tableQuery.sortBy(_.id).take(100).result).map(_.toSet)
  }

  override def addOne(lastName: LastName): Future[LastName] = {
    val lastNameJdbc = LastNameJdbc(UUID.randomUUID(), lastName.lastName)
    val value = tableQuery += lastNameJdbc
    db.run(value).map(_ => lastNameJdbc)
  }

}
