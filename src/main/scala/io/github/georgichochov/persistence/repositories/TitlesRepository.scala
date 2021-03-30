package io.github.georgichochov.persistence.repositories

import io.github.georgichochov.models.Title

import scala.concurrent.Future

trait TitlesRepository {

  def fetchAll: Future[Set[Title]]

  def fetch100: Future[Set[Title]]

  def addOne(title: Title): Future[Title]

}
