package api

import com.lightbend.lagom.scaladsl.persistence.{AggregateEventTag, ReadSideProcessor}

class UserEventProcessor extends ReadSideProcessor {
  override def buildHandler(): ReadSideProcessor.ReadSideHandler[Nothing] = ???

  override def aggregateTags: Set[AggregateEventTag[Nothing]] = ???
}
