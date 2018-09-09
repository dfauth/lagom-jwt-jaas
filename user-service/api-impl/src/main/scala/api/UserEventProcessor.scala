package api

import com.lightbend.lagom.scaladsl.persistence.slick.SlickReadSide
import com.lightbend.lagom.scaladsl.persistence.{AggregateEventTag, ReadSideProcessor}

class UserEventProcessor(
                          readSide: SlickReadSide,
                          userRepo: UserRepository

                        ) extends ReadSideProcessor[UserEvent] {

  override def buildHandler(): ReadSideProcessor.ReadSideHandler[UserEvent] = ???

  override def aggregateTags: Set[AggregateEventTag[UserEvent]] = ???
}
