package api

import com.lightbend.lagom.scaladsl.persistence.ReadSideProcessor.ReadSideHandler
import com.lightbend.lagom.scaladsl.persistence.slick.SlickReadSide
import com.lightbend.lagom.scaladsl.persistence.{AggregateEventTag, EventStreamElement, ReadSideProcessor}
import slick.dbio.{DBIOAction, NoStream}

class UserEventProcessor(
                          readSide: SlickReadSide,
                          userRepo: UserRepository

                        ) extends ReadSideProcessor[UserEvent] {

  override def buildHandler(): ReadSideHandler[UserEvent] = {
    readSide.builder[UserEvent]("userEventOffset")
      .setPrepare { tag =>
        prepareStatements()
      }.setEventHandler[UserCreated](insertUser)
      .build()
  }

  override def aggregateTags: Set[AggregateEventTag[UserEvent]] = {
    UserEvent.Tag.allTags
  }

  def prepareStatements(): DBIOAction[Any, NoStream, Nothing] = ???

  //  private def prepareStatements(): Future[Done] = {
//    for {
//      insertUser <- session.prepare("INSERT INTO users(id, client_id, username, email, first_name, last_name, hashed_password) VALUES (?, ?, ?, ?, ?, ?, ?)")
//      reportIdToReservedUsernames <- session.prepare("UPDATE reserved_usernames SET user_id = ? WHERE username = ?")
//      reportIdToReservedEmails <- session.prepare("UPDATE reserved_emails SET user_id = ? WHERE email = ?")
//    } yield {
//      insertUserStatement = insertUser
//      reportIdToReservedUsernamesStatement = reportIdToReservedUsernames
//      reportIdToReservedEmailsStatement = reportIdToReservedEmails
//      Done
//    }
//  }

  def insertUser: EventStreamElement[UserCreated] => DBIOAction[Any, NoStream, Nothing] = {
    null
  }

  //  private def insertUser(user: EventStreamElement[UserCreated]) = {
//    Future.successful(
//      List(
//        insertUserStatement.bind(
//          user.event.userId,
//          UUID.fromString(user.entityId),
//          user.event.username,
//          user.event.email,
//          user.event.firstName,
//          user.event.lastName,
//          user.event.hashedPassword
//        ),
//        reportIdToReservedUsernamesStatement.bind(user.event.userId, user.event.username),
//        reportIdToReservedEmailsStatement.bind(user.event.userId, user.event.email)
//      )
//    )
//  }

}
