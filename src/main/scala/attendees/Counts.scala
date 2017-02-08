package attendees

import play.api.libs.json.{Format, Json}

import scala.language.postfixOps

@SerialVersionUID(42L)
final case class Counts(eventFacebookId: String,
                        attending_count: Long,
                        declined_count : Long,
                        interested_count: Long,
                        noreply_count: Long)

object Counts {
  implicit val countsFormat: Format[Counts] = Json.format[Counts]
}
