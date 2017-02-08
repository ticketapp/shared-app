package artistsDomain

import genresDomain.{Genre, GenreWithWeight}
import play.api.libs.json.{Format, Json}

@SerialVersionUID(42L)
final case class Artist(facebookId: String,
                        name: String,
                        imagePath: Option[String] = None,
                        description: Option[String] = None,
                        facebookUrl: String,
                        websites: Set[String] = Set.empty,
                        hasTracks: Boolean = false,
                        likes: Option[Long] = None,
                        country: Option[String] = None)

object Artist {
  implicit val artistFormat: Format[Artist] = Json.format[Artist]
}

@SerialVersionUID(42L)
final case class ArtistWithWeightedGenres(artist: Artist, genres: Seq[GenreWithWeight] = Seq.empty)

object ArtistWithWeightedGenres {
  implicit val artistWithGenresFormat = Json.format[ArtistWithWeightedGenres]
}

final case class PatternAndArtist(searchPattern: String, artistWithWeightedGenres: ArtistWithWeightedGenres)

@SerialVersionUID(42L)
final case class EventIdArtistsAndGenres(eventId: String,
                                         artistsWithWeightedGenres: Seq[ArtistWithWeightedGenres],
                                         genresWithWeight: Seq[Genre])
