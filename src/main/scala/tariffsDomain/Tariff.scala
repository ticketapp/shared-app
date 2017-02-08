package tariffsDomain

import org.joda.time.DateTime
import play.api.libs.json.{Format, Json}

final case class Tariff(tariffId: Option[Long] = None,
                        denomination: String,
                        eventId: String,
                        startTime: DateTime,
                        endTime: DateTime,
                        price: BigDecimal,
                        basePrice: BigDecimal,
                        placeFacebookUrl: Option[String] = None,
                        barCodeTypes: Seq[String] = Seq("qrCode", "c128b"))

object Tariff {
  implicit val tariffFormat: Format[Tariff] = Json.format[Tariff]
}

object Currency extends Enumeration {
  sealed abstract class Val extends Enumeration
  case object EUR extends Val
  case object GPB extends Val
  case object USD extends Val
}
