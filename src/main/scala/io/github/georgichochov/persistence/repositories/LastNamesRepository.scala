package io.github.georgichochov.persistence.repositories

import io.github.georgichochov.models.LastName

import scala.concurrent.Future

trait LastNamesRepository {

  def fetchAll: Future[Set[LastName]]

  def fetch100: Future[Set[LastName]]

  def addOne(lastName: LastName): Future[LastName]

}
