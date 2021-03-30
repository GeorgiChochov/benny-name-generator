package io.github.georgichochov.bennycore

import spray.json.DefaultJsonProtocol.{
  IntJsonFormat,
  StringJsonFormat,
  jsonFormat3
}

case class BennyName(
    title: String,
    fancyLastName: String,
    dynasticNumeral: Int
) {
  def fullName: String = s"$title Benedict $fancyLastName $dynasticNumeral"
}

object BennyName {
  implicit val foramt = jsonFormat3(BennyName.apply)
}
