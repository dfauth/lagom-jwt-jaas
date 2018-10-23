package api

import api.repo.{User, UserRepository}
import com.lightbend.lagom.scaladsl.persistence.ReadSideProcessor.ReadSideHandler
import com.lightbend.lagom.scaladsl.persistence.slick.SlickReadSide
import com.lightbend.lagom.scaladsl.persistence.{AggregateEventTag, EventStreamElement, ReadSideProcessor}
import log.Logging
import slick.dbio.{DBIOAction, NoStream}

import scala.concurrent.{Await, duration}
import scala.concurrent.duration.Duration

class UserEventProcessor(
                          readSide: SlickReadSide,
                          userRepo: UserRepository

                        ) extends ReadSideProcessor[UserEvent] with Logging {

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

    import scala.concurrent.ExecutionContext.Implicits.global

    Await.result(userRepo.probe().map(n => DBIOAction.seq()).recover{
      case t:Throwable => userRepo.init()
    }, Duration(10, duration.SECONDS))
  }

  def insertUser: EventStreamElement[UserCreated] => DBIOAction[Any, NoStream, Nothing] = {
    e => userRepo.insert(new User(email = e.event.email,
                                  firstName = Option(e.event.firstName),
                                  lastName = Option(e.event.lastName)
    ))
  }

}


