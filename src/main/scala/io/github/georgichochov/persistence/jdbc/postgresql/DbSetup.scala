package io.github.georgichochov.persistence.jdbc.postgresql

import slick.jdbc.JdbcBackend
import slick.jdbc.JdbcBackend.Database

import java.net.URI

class DbSetup {

  def setUp(dbUrl: String): JdbcBackend.DatabaseDef = {
    val dbUri = new URI(dbUrl)
    val Array(username, password) = dbUri.getUserInfo.split(":")
    val connStr =
      s"jdbc:postgresql://${dbUri.getHost}:${dbUri.getPort}${dbUri.getPath}"

    Database.forURL(
      connStr,
      user = username,
      password = password,
      driver = "org.postgresql.Driver"
    )
  }

}
