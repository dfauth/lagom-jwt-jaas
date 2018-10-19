package api

import com.lightbend.lagom.scaladsl.persistence.{AggregateEvent, AggregateEventShards, AggregateEventTag}
import play.api.libs.json.{Format, Json}

object UserEvent {
  val NumShards = 5
  val Tag = AggregateEventTag.sharded[UserEvent]("UserEvent", NumShards)
}

sealed trait UserEvent extends AggregateEvent[UserEvent] {
  override def aggregateTag(): AggregateEventShards[UserEvent] = UserEvent.Tag
}

case class ClientCreated(company: String) extends UserEvent
object ClientCreated {
  implicit val format: Format[ClientCreated] = Json.format
}

case class UserCreated(firstName: String, lastName: String, email: String, username: String, password: String) extends UserEvent

object UserCreated {
  implicit val format: Format[UserCreated] = Json.format
}
