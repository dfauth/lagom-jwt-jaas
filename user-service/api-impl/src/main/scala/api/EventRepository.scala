package api

import api.repo.Db
import slick.basic.DatabaseConfig
import slick.dbio.DBIOAction
import slick.jdbc.JdbcProfile

case class Event(id: Option[Int] = None, eventType: String, payload:String)

case class UsernameUniqueness(id: Option[Int] = None, username:String)

trait EventTable { this: Db =>
  import config.driver.api._

  class Events(tag: Tag) extends Table[Event](tag, "EVENTS") {
    // Columns
    def id = column[Int]("EVENT_ID", O.PrimaryKey, O.AutoInc)
    def eventType = column[String]("TYPE", O.Length(64))
    def payload = column[String]("PAYLOAD", O.Length(1024))

    // Select
    def * = (id.?, eventType, payload) <> (Event.tupled, Event.unapply)
  }

  val events = TableQuery[Events]
}

trait UsernameUniquenessTable { this: Db =>
  import config.driver.api._

  class UsernameUniquenesses(tag: Tag) extends Table[UsernameUniqueness](tag, "USERNAME_UNIQUENESS") {
    // Columns
    def id = column[Int]("ID", O.PrimaryKey, O.AutoInc)
    def username = column[String]("USERNAME", O.Length(64), O.Unique)

    // Select
    def * = (id.?, username) <> (UsernameUniqueness.tupled, UsernameUniqueness.unapply)
  }

  val usernameUniqueness = TableQuery[UsernameUniquenesses]
}



class EventRepository(val config: DatabaseConfig[JdbcProfile])
  extends Db with EventTable with UsernameUniquenessTable {

  import config.driver.api._

  import scala.concurrent.ExecutionContext.Implicits.global

  // ...
  def init() = db.run(
    DBIOAction.seq(events.schema.create).andThen(
      DBIOAction.seq(usernameUniqueness.schema.create)))
  def drop() = db.run(
    DBIOAction.seq(events.schema.drop).andThen(
      DBIOAction.seq(usernameUniqueness.schema.drop)))

  def insert(event:Event) = db
    .run(events returning events.map(_.id) += event)
    .map(id => event.copy(id = Some(id)))

}
