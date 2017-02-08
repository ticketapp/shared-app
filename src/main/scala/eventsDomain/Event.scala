package eventsDomain

import addresses.{Address, GeographicPointTrait}
import artistsDomain.ArtistWithWeightedGenres
import attendees.Counts
import com.vividsolutions.jts.geom.{Coordinate, Geometry, GeometryFactory, PrecisionModel}
import genresDomain.Genre
import org.joda.time.DateTime
import organizersDomain.OrganizerWithAddress
import placesDomain.PlaceWithAddress
import play.api.libs.json.{Format, Json, OFormat}
import services.SortableByGeographicPoint
import tariffsDomain.Tariff

import scala.language.postfixOps

@SerialVersionUID(42L)
final case class Event(id: Option[Long] = None,
                       facebookId: String,
                       name: String,
                       var geographicPoint: Geometry =
                         new GeometryFactory(new PrecisionModel(), 4326).createPoint(new Coordinate(-84, 30)),
                       description: Option[String] = None,
                       startTime: DateTime,
                       endTime: Option[DateTime] = None,
                       ageRestriction: Int = 16,
                       tariffRange: Option[String] = None,
                       ticketSellers: Option[String] = None,
                       imagePath: Option[String] = None,
                       currency: Option[String] = None)

object Event {
  import json.JsonHelper.geometryJsonFormat
  implicit val eventFormat: OFormat[Event] = Json.format[Event]
}

@SerialVersionUID(42L)
case class EventIdNameAndCount(eventFacebookId: String, name: String, attendeesCount: Counts)
object EventIdNameAndCount {
  implicit val eventIdAndNameFormat: Format[EventIdNameAndCount] = Json.format[EventIdNameAndCount]
}

@SerialVersionUID(42L)
final case class EventWithRelations(event: Event,
                                    organizers: Seq[OrganizerWithAddress] = Vector.empty,
                                    artists: Seq[ArtistWithWeightedGenres] = Vector.empty,
                                    places: Seq[PlaceWithAddress] = Vector.empty,
                                    genres: Seq[Genre] = Vector.empty,
                                    addresses: Seq[Address] = Vector.empty,
                                    counts: Option[Counts] = None,
                                    tariffs: Seq[Tariff] = Vector.empty)
    extends SortableByGeographicPoint with GeographicPointTrait {

  private def returnEventGeographicPointInRelations(event: Event,
                                                    addresses: Seq[Address],
                                                    places: Seq[PlaceWithAddress]): Geometry =
    event.geographicPoint match {
      case notAntarcticPoint if notAntarcticPoint != antarcticPoint =>
        notAntarcticPoint

      case _ =>
        val addressesGeoPoints = addresses map(_.geographicPoint)
        val placesGeoPoint = places.map(_.geographicPoint)
        val geoPoints = addressesGeoPoints ++ placesGeoPoint

        geoPoints find(_ != antarcticPoint) match {
          case Some(geoPoint) => geoPoint
          case _ => antarcticPoint
        }
    }

  val geographicPoint: Geometry = returnEventGeographicPointInRelations(event, addresses, places)
  this.event.geographicPoint = geographicPoint
}

object EventWithRelations {
  import json.JsonHelper.geometryJsonFormat
  implicit val eventFormat: OFormat[Event] = Json.format[Event]
  implicit val eventWithRelationsFormat: OFormat[EventWithRelations] = Json.format[EventWithRelations]
}

@SerialVersionUID(42L)
final case class EventAndPlaceFacebookUrl(event: EventWithRelations, placeFacebookUrl: String)
