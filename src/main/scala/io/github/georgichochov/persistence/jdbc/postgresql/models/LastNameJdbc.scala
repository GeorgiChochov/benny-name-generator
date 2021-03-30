package io.github.georgichochov.persistence.jdbc.postgresql.models

import io.github.georgichochov.models.LastName

import java.util.UUID

case class LastNameJdbc(id: UUID, lastName: String) extends LastName
