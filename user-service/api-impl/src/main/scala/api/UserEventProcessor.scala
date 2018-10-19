package api

import api.repo.UserRepository
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


/**

public class CustomerEventProcessor extends ReadSideProcessor<CustomerEvent> {

   private final JdbcReadSide readSide;

   @Inject
   public CustomerEventProcessor(JdbcReadSide readSide) {
       this.readSide = readSide;
   }

@Override
public ReadSideHandler<CustomerEvent> buildHandler() {
       JdbcReadSide.ReadSideHandlerBuilder<CustomerEvent> builder = readSide.builder("votesoffset");

       builder.setGlobalPrepare(this::createTable);
       builder.setEventHandler(CustomerEvent.AddedCustomerEvent.class, this::processCustomerAdded);

       return builder.build();
   }

   private void createTable(Connection connection) throws SQLException {
       connection.prepareStatement(
               "CREATE TABLE IF NOT EXISTS customers ( "
                       + "id MEDIUMINT NOT NULL AUTO_INCREMENT, "
                       + "email VARCHAR(64) NOT NULL, "
                       + "firstname VARCHAR(64) NOT NULL, "
                       + "lastname VARCHAR(64) NOT NULL, "
                       + "birthdate DATETIME NOT NULL, "
                       + "comment VARCHAR(256), "
                       + "dt_created DATETIME DEFAULT CURRENT_TIMESTAMP, "
                       + " PRIMARY KEY (id))").execute();
   }

   private void processCustomerAdded(Connection connection, CustomerEvent.AddedCustomerEvent event) throws SQLException {
       PreparedStatement statement = connection.prepareStatement(
               "INSERT INTO customers (email, firstname, lastname, birthdate, comment) VALUES (?, ?, ?, ?, ?)");
       statement.setString(1, event.email);
       statement.setString(2, event.firstName);
       statement.setString(3, event.lastName);
       statement.setDate(4, event.birthDate);
       statement.setString(5, event.comment.orElse(""));
       statement.execute();
   }

@Override
public PSequence<AggregateEventTag<CustomerEvent>> aggregateTags() {
       return TreePVector.singleton(CustomerEvent.CUSTOMER_EVENT_TAG);
   }
}



  */