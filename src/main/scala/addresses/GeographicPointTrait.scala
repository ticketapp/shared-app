package addresses

import com.vividsolutions.jts.geom._
import org.slf4j.LoggerFactory

import scala.util.{Failure, Success, Try}

trait GeographicPointTrait {
  private val geometryFactory: GeometryFactory = new GeometryFactory(new PrecisionModel(), 4326)
  val antarcticPoint: Point = geometryFactory.createPoint(new Coordinate(-84, 30))

  def stringToTryPoint(string: String): Try[Geometry] = Try {
    val latitudeAndLongitude: Array[String] = string.split(",")
    latAndLngToGeographicPoint(latitudeAndLongitude(0).toDouble, latitudeAndLongitude(1).toDouble) match {
      case Success(point) => point
      case Failure(failure) => throw failure
    }
  }

  def createPolygon(minX: Double, maxX: Double, minY: Double, maxY: Double): Polygon =
    geometryFactory.createPolygon(geometryFactory.toGeometry(new Envelope(minX, maxX, minY, maxY)).getCoordinates)

  def latAndLngToGeographicPoint(latitude: Double, longitude: Double): Try[Geometry] = Try {
    val coordinate = new Coordinate(latitude, longitude)
    geometryFactory.createPoint(coordinate)
  }

  def optionStringToPoint(maybeGeographicPoint: Option[String]): Geometry = maybeGeographicPoint match {
    case Some(geoPoint) =>
      stringToTryPoint(geoPoint) match {
        case Failure(exception) =>
          LoggerFactory.getLogger("application").error("Utilities.optionStringToPoint: ", exception)
          antarcticPoint

        case Success(validPoint) =>
          validPoint
      }

    case _ =>
      antarcticPoint
  }
}
