package interAppsClasses

import addresses.Address
import com.vividsolutions.jts.geom.Polygon
import eventsDomain.Event

@SerialVersionUID(42L)
final case class UpdateEvent(event: Event, offset: Long)

@SerialVersionUID(42L)
final case class MaybeEventWithOffset(event: Option[Event], offset: Long)

@SerialVersionUID(42L)
final case class GetEventWithoutRelations(offset: Long)

@SerialVersionUID(42L)
final case class GetUpcomingSalableEvents(offset: Long, numberToReturn: Int)

//Used by : priceDecision 
@SerialVersionUID(42L)
final case class GetPastEventsInZone(polygon: Polygon, offset: Long, numberToReturn: Int)

@SerialVersionUID(42L)
final case class GetPopularPastEventsInZone(polygon: Polygon, offset: Long, numberToReturn: Int)

//Used by : priceDecision
@SerialVersionUID(42L)
final case class GetUpcomingPopularEventsInZone(polygon: Polygon, offset: Long, numberToReturn: Int, minAttending: Int)

//Used by : priceDecision
@SerialVersionUID(42L)
final case class GetUpcomingEventsInZone(polygon: Polygon, offset: Long, numberToReturn: Int)

@SerialVersionUID(42L)
final case class WikipediaPlace(venue: String, room: Option[String] = None, address: Address, capacity: Int)

@SerialVersionUID(42L)
case object IncrementCounter

@SerialVersionUID(42L)
final case class GetEventsByIds(ids: Seq[String])

@SerialVersionUID(42L)
final case class GetEventsById(id: String)

@SerialVersionUID(42L)
final case class GetEventsByOrganizer(id: String)

@SerialVersionUID(42L)
case object Finish

@SerialVersionUID(42L)
final case class GetEventsFacebookIdByPlace(placeFacebookId: String)
