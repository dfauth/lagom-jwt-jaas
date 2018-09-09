package api

import java.sql.Timestamp
import java.util.UUID

import api.date.DateUtcUtil
import slick.jdbc.H2Profile.api._
import slick.jdbc.meta.MTable

import scala.concurrent.Future
import scala.concurrent.ExecutionContext.Implicits.global

class UserRepository(db:Database) {

  def findUserByUsername(username: String): Future[Option[UserByUsername]] = {
//    val result = db.selectOne("SELECT id, client_id, username, hashed_password FROM users_by_username WHERE username = ?", username).map {
//      case Some(row) => Option(
//        UserByUsername(
//          username = row.getString("username"),
//          id = row.getUUID("id"),
//          clientId = row.getUUID("client_id"),
//          hashedPassword = row.getString("hashed_password")
//        )
//      )
//      case None => Option.empty
//    }
//
//    result
    null
  }

  def reserveUsername(username: String): Future[Boolean] = {
    val createdOn = new Timestamp(DateUtcUtil.now().getMillis)

//    def insert(user: User) = db.run(users += user)
    Future[Boolean] {
      val result = reservedUsernames += (username, createdOn)
      result.map[Boolean](i => true)
      true
    }

//    val setup = DBIO.seq(
//
//      // Insert some suppliers
//      reservedUsernames += (username, createdOn)
//    )
//
//    db.run(setup)
//    Future(true)

    //    db.selectOne("INSERT INTO reserved_usernames (username, created_on) VALUES (?, ?) IF NOT EXISTS", username, createdOn).map {
//      case Some(row) => row.getBool("[applied]")
//      case None => false
//    }
  }

  def unreserveUsername(username: String) = {
//    db.executeWrite("DELETE FROM reserved_usernames WHERE username = ?", username)
    null
  }

  def reserveEmail(email: String): Future[Boolean] = {
    val createdOn = new Timestamp(DateUtcUtil.now().getMillis)

//    db.selectOne("INSERT INTO reserved_emails (email, created_on) VALUES (?, ?) IF NOT EXISTS", email, createdOn).map {
//      case Some(row) => row.getBool("[applied]")
//      case None => false
//    }
    null
  }

  def unreserveEmail(email: String) = {
//    db.executeWrite("DELETE FROM reserved_emails WHERE email = ?", email)
  }

  class ReservedUsernames(tag: Tag)
    extends Table[(String, Timestamp)](tag, "reserved_usernames") {

    def username = column[String]("username", O.PrimaryKey)
    def created_on = column[Timestamp]("created_on")
    def * = (username, created_on)
  }

  val reservedUsernames = TableQuery[ReservedUsernames]

  class ReservedEmails(tag: Tag)
    extends Table[(String, Timestamp)](tag, "reserved_emails") {

    def email = column[String]("email", O.PrimaryKey)
    def created_on = column[Timestamp]("created_on")
    def * = (email, created_on)
  }

  val reservedEmails = TableQuery[ReservedEmails]

  def selectUsers() = reservedUsernames.result

  def createTable = {
    MTable.getTables.flatMap { tables =>
      if (!tables.exists(_.name.name == reservedUsernames.baseTableRow.tableName)) {
        reservedUsernames.schema.create
      } else {
        DBIO.successful(())
      }
    }.transactionally
    MTable.getTables.flatMap { tables =>
      if (!tables.exists(_.name.name == reservedEmails.baseTableRow.tableName)) {
        reservedEmails.schema.create
      } else {
        DBIO.successful(())
      }
    }.transactionally
  }
}

case class UserByUsername(username: String, id: UUID, clientId: UUID, hashedPassword: String)
