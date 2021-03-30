package io.github.georgichochov.persistence.jdbc.postgresql.models

import io.github.georgichochov.models.Title

import java.util.UUID

case class TitleJdbc(id: UUID, title: String) extends Title
