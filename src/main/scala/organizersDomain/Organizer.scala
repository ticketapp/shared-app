package organizersDomain

import addresses.{Address, GeographicPointTrait}
import com.vividsolutions.jts.geom.{Coordinate, Geometry, GeometryFactory, PrecisionModel}
import play.api.libs.json.{Json, OFormat}
import services.SortableByGeographicPoint

final case class Organizer(id: Option[Long] = None,
                           facebookId: String,
                           facebookUrl: String,
                           name: String,
                           description: Option[String] = None,
                           addressId: Option[Long] = None,
                           phone: Option[String] = None,
                           publicTransit: Option[String] = None,
                           websites: Option[String] = None,
                           verified: Boolean = false,
                           imagePath: Option[String] = None,
                           var geographicPoint: Geometry =
                             new GeometryFactory(new PrecisionModel(), 4326).createPoint(new Coordinate(-84, 30)),
                           linkedPlaceUrl: Option[String] = None,
                           likes: Option[Long] = None)
object Organizer {
  import json.JsonHelper.geometryJsonFormat
  implicit val organizerFormat: OFormat[Organizer] = Json.format[Organizer]
}

@SerialVersionUID(42L)
final case class OrganizerWithAddress(organizer: Organizer,
                                      maybeAddress: Option[Address] = None)
  extends SortableByGeographicPoint with GeographicPointTrait{
  private def returnEventGeographicPointInRelations(organizer: Organizer,
                                                    maybeAddress: Option[Address]): Geometry =
    organizer.geographicPoint match {
      case notAntarcticPoint if notAntarcticPoint != antarcticPoint =>
        notAntarcticPoint

      case _ =>
        val addressesGeoPoints = maybeAddress map(_.geographicPoint)
        val organizerGeoPoint = Option(organizer.geographicPoint)
        val geoPoints = Seq(addressesGeoPoints, organizerGeoPoint).flatten

        geoPoints find(_ != antarcticPoint) match {
          case Some(geoPoint) => geoPoint
          case _ => antarcticPoint
        }
    }

  val geographicPoint: Geometry = returnEventGeographicPointInRelations(organizer, maybeAddress)
  this.organizer.geographicPoint = geographicPoint
}

object OrganizerWithAddress {
  implicit val organizerWithAddressFormat: OFormat[OrganizerWithAddress] = Json.format[OrganizerWithAddress]
  def tupled: ((Organizer, Option[Address])) => OrganizerWithAddress = (OrganizerWithAddress.apply _).tupled
}
