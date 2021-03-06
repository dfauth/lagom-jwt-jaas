package api

import api.repo.{Role, User, UserRepository}
import com.lightbend.lagom.scaladsl.persistence.ReadSideProcessor.ReadSideHandler
import com.lightbend.lagom.scaladsl.persistence.slick.SlickReadSide
import com.lightbend.lagom.scaladsl.persistence.{AggregateEventTag, EventStreamElement, ReadSideProcessor}
import log.Logging
import util.PasswordHashing.hashPassword
import slick.dbio.{DBIOAction, NoStream}

import scala.concurrent.duration.Duration
import scala.concurrent.{Await, duration}

class UserEventProcessor(
                          readSide: SlickReadSide,
                          userRepo: UserRepository

                        ) extends ReadSideProcessor[UserEvent] with Logging {

  override def buildHandler(): ReadSideHandler[UserEvent] = {
    readSide.builder[UserEvent]("userEventOffset")
        .setGlobalPrepare(prepareStatements())
      .setEventHandler[UserCreated](insertUser)
      .setEventHandler[RoleCreated](insertRole)
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
                                  lastName = Option(e.event.lastName),
                                  hashedPassword = hashPassword(e.event.password)
    ))
  }

  def insertRole: EventStreamElement[RoleCreated] => DBIOAction[Any, NoStream, Nothing] = {
    e => userRepo.insert(new Role(roleName = e.event.roleName, description = Some(e.event.description)))
  }

}


