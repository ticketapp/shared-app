package json

import java.sql.Timestamp

import akka.actor.ActorRef
import com.vividsolutions.jts.geom.{Geometry, Point}
import com.vividsolutions.jts.io.{WKTReader, WKTWriter}
import org.joda.time.DateTime
import play.api.libs.json.{JsNumber, _}

object JsonHelper {
  implicit object JavaBigDecimalWrites extends Writes[java.math.BigDecimal] {
    def writes(bigDecimal: java.math.BigDecimal): JsNumber = JsNumber(BigDecimal(bigDecimal))
  }

  implicit object CharWrites extends Writes[Char] {
    def writes(char: Char): JsString = JsString(char.toString)
  }

  implicit object CharReads extends Reads[Char] {
    def reads(char: JsValue): JsResult[Char] = JsSuccess(Json.stringify(char)(1))
  }

  implicit val charFormat = new Format[Char] {
    def reads(jsValue: JsValue): JsResult[Char] = CharReads.reads(jsValue)

    def writes(char: Char): JsString = CharWrites.writes(char)
  }

  implicit object ActorRefWrites extends Writes[ActorRef] {
    def writes(actorRef: ActorRef): JsString = JsString(actorRef.toString)
  }

  implicit object TimestampReads extends Reads[Timestamp] {
    def reads(jsValue: JsValue): JsResult[Timestamp] = JsSuccess(new Timestamp(Json.stringify(jsValue).toLong))
  }

  implicit val timestampFormat = new Format[Timestamp] {
    def timestampToDateTime(t: Timestamp): DateTime = new DateTime(t.getTime)

    def writes(t: Timestamp): JsValue = Json.toJson(timestampToDateTime(t))

    def reads(jsValue: JsValue): JsResult[Timestamp] =
      JsSuccess(new Timestamp(new DateTime(jsValue.toString.stripPrefix("\"").stripSuffix("\"")).getMillis))
  }

  def geomJsonFormat[G <: Geometry]: Format[G] = Format[G](
    fjs = Reads.StringReads.map(fromWKT[G]),
    tjs = new Writes[G] {
      def writes(o: G): JsValue = JsString(toWKT(o))
    })

  private val wktWriterHolder = new ThreadLocal[WKTWriter]
  private val wktReaderHolder = new ThreadLocal[WKTReader]

  private def toWKT(geom: Geometry): String = {
    if (wktWriterHolder.get == null) wktWriterHolder.set(new WKTWriter())
    wktWriterHolder.get.write(geom)
  }

  private def fromWKT[T](wkt: String): T = {
    if (wktReaderHolder.get == null) wktReaderHolder.set(new WKTReader())
    val geometry = wktReaderHolder.get.read(wkt).asInstanceOf[T]
    geometry match {
      case point: Point =>
        point.setSRID(4326)
        point.asInstanceOf[T]

      case otherGeometry =>
        otherGeometry
    }
  }

  implicit val geometryJsonFormat: Format[Geometry] = geomJsonFormat[Geometry]

  def enumReads[E <: Enumeration](enum: E): Reads[E#Value] = new Reads[E#Value] {
    def reads(json: JsValue): JsResult[E#Value] = json match {
      case JsString(s) =>
        try JsSuccess(enum.withName(s)) catch { case _: NoSuchElementException =>
          JsError(s"Enumeration expected of type: '${enum.getClass}', but it does not appear to contain the value: '$s'")
        }

      case _ =>
        JsError("String value expected")
    }
  }

  implicit def enumWrites[E <: Enumeration]: Writes[E#Value] = new Writes[E#Value] {
    def writes(v: E#Value): JsValue = JsString(v.toString)
  }

  val dateTimeFormat = "yyyy-MM-dd'T'HH:mm:ssZ"
  implicit val jodaDateReads: Reads[DateTime] = Reads.jodaDateReads(dateTimeFormat)
  implicit val jodaDateWrites: Writes[DateTime] = Writes.jodaDateWrites(dateTimeFormat)
}
