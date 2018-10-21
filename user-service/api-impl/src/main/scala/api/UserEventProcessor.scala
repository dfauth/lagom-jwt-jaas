package api

import api.repo.{User, UserRepository}
import com.lightbend.lagom.scaladsl.persistence.ReadSideProcessor.ReadSideHandler
import com.lightbend.lagom.scaladsl.persistence.slick.SlickReadSide
import com.lightbend.lagom.scaladsl.persistence.{AggregateEventTag, EventStreamElement, ReadSideProcessor}
import slick.dbio.{DBIOAction, NoStream}

import scala.util.{Failure, Success}

class UserEventProcessor(
                          readSide: SlickReadSide,
                          userRepo: UserRepository

                        ) extends ReadSideProcessor[UserEvent] {

  override def buildHandler(): ReadSideHandler[UserEvent] = {
    readSide.builder[UserEvent]("userEventOffset")
        .setGlobalPrepare(prepareStatements())
      .setEventHandler[UserCreated](insertUser)
      .build()
  }

  override def aggregateTags: Set[AggregateEventTag[UserEvent]] = {
    UserEvent.Tag.allTags
  }

  def prepareStatements(): DBIOAction[Any, NoStream, Nothing] = {
    userRepo.probe() match {
      case Success(f) => DBIOAction.seq()
      case Failure(t) => userRepo.init()
    }
  }

  def insertUser: EventStreamElement[UserCreated] => DBIOAction[Any, NoStream, Nothing] = {
    e => userRepo.insert(new User(email = e.event.email,
                                  firstName = Option(e.event.firstName),
                                  lastName = Option(e.event.lastName)
    ))
  }

}


