package genresDomain

import play.api.libs.json.{Json, OFormat}

import scala.language.postfixOps

case class Genre (id: Option[Int] = None, name: String, icon: Char = 'a') {
  require(name.nonEmpty, "It is forbidden to create a genre without a name.")
}

object Genre {
  import json.JsonHelper.{CharReads, CharWrites}
  implicit val genreFormat: OFormat[Genre] = Json.format[Genre]
}

case class GenreWithWeight(genre: Genre, weight: Int = 1)

object GenreWithWeight {
  implicit val genreWithWeightFormat: OFormat[GenreWithWeight] = Json.format[GenreWithWeight]
}
