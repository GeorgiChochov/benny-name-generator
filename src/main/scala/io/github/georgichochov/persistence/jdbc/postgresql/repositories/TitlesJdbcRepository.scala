package io.github.georgichochov.persistence.jdbc.postgresql.repositories

import io.github.georgichochov.models.Title
import io.github.georgichochov.persistence.jdbc.postgresql.models.TitleJdbc
import io.github.georgichochov.persistence.jdbc.postgresql.tables.Titles
import io.github.georgichochov.persistence.repositories.TitlesRepository
import slick.jdbc.JdbcBackend
import slick.jdbc.PostgresProfile.api._
import slick.lifted.TableQuery

import java.util.UUID
import scala.concurrent.{ExecutionContext, Future}

class TitlesJdbcRepository(db: JdbcBackend.DatabaseDef)(implicit
    ec: ExecutionContext
) extends TitlesRepository {

  private val tableQuery = TableQuery[Titles]
  val schema = tableQuery.schema

  override def fetchAll: Future[Set[Title]] = {
    db.run(tableQuery.result).map(_.toSet)
  }

  override def fetch100: Future[Set[Title]] = {
    db.run(tableQuery.sortBy(_.id).take(100).result).map(_.toSet)
  }

  override def addOne(title: Title): Future[Title] = {
    val titleJdbc = TitleJdbc(UUID.randomUUID(), title.title)
    val value = tableQuery += titleJdbc
    db.run(value).map(_ => titleJdbc)
  }

}
