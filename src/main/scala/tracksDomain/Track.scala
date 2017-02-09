package tracksDomain

import java.util.UUID

import genresDomain.Genre
import play.api.libs.json.{Format, Json}

final case class Track(uuid: UUID,
                       title: String,
                       url: String,
                       platform: Char,
                       thumbnailUrl: String,
                       artistFacebookUrl: String,
                       artistName: String,
                       redirectUrl: Option[String] = None,
                       confidence: Double = 0.toDouble)

object Track {
  import json.JsonHelper.charFormat
  implicit val trackFormat: Format[Track] = Json.format[Track]
}

final case class TrackWithGenres(track: Track, genres: Seq[Genre])
