package addresses

import com.vividsolutions.jts.geom.{Coordinate, Geometry, GeometryFactory, PrecisionModel}
import play.api.libs.json.{Json, OFormat}

import scala.language.postfixOps

final case class Address(id: Option[Long] = None,
                         geographicPoint: Geometry =
                           new GeometryFactory(new PrecisionModel(), 4326).createPoint(new Coordinate(-84, 30)),
                         city: Option[String] = None,
                         zip: Option[String] = None,
                         street: Option[String] = None,
                         country: Option[String] = None) {
  require(
    !(geographicPoint == new GeometryFactory(new PrecisionModel(), 4326).createPoint(new Coordinate(-84, 30)) &&
      city.isEmpty && zip.isEmpty && street.isEmpty && country.isEmpty),
    "address must contain at least one field")
}

object Address {
  import json.JsonHelper.geometryJsonFormat
  implicit val addressFormat: OFormat[Address] = Json.format[Address]
}
