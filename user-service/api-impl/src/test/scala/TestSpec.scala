
import akka.stream.Materializer
import akka.stream.scaladsl.{Sink, Source}
import org.apache.logging.log4j.scala.Logging
import org.scalatest._
import slick.basic.DatabaseConfig
import slick.dbio.DBIOAction
import slick.jdbc.JdbcProfile

import scala.concurrent.Await
import scala.concurrent.duration._


class TestSpec extends FlatSpec with DbConfiguration with Matchers with Logging {

  val timeout = 500.milliseconds
  val repo = new UserRepository(config)

  def beforeTest = {
    Await.result(repo.init(), timeout)
  }

  def afterTest = {
    Await.result(repo.drop(), timeout)
  }

  "User" should "be inserted successfully" in {
    import scala.concurrent.ExecutionContext.Implicits.global

    try {
      beforeTest

      var user = User(email = "fred@flintstone.com", firstName = Some("Fred"), lastName = Some("Flintstone"))
      var user1 = User(email = "wilma@flintstone.com", firstName = Some("Wilma"), lastName = Some("Flintstone"))
      var role = Role(roleName = "admin")
      var role1 = Role(roleName = "role1")
      val userFuture = repo.insert(user)
      val user1Future = repo.insert(user1)
      val roleFuture = repo.insert(role)
      val role1Future = repo.insert(role1)
      val userRoleFuture = for{
        u <- userFuture
        r <- roleFuture
        u1 <- user1Future
        r1 <- role1Future
      } yield {
        user = u
        user1 = u1
        role = r
        role1 = r1
        repo.insert(u, r)
        repo.insert(u1, r1)
      }
      val users = Await.result(userFuture, 20.seconds)
      val allUsers = Await.result(repo.findUsers, 20.seconds)
      logger.info("query all users:"+allUsers)
      allUsers.size should be (2)

      val roles = Await.result(roleFuture, 20.seconds)
      val allRoles = Await.result(repo.findRoles, 20.seconds)
      logger.info("query all roles:"+allRoles)
      allRoles.size should be (2)

      Await.result(userRoleFuture, 20.seconds)

      val myRolesF = repo.findRolesForUser(user)
      val myRoles = Await.result(myRolesF, 20.seconds)
      myRoles.size should be (1)

      logger.info("query myRoles: "+myRoles)
    } finally {
      afterTest
    }
  }

}

trait DbConfiguration {
  lazy val config = DatabaseConfig.forConfig[JdbcProfile]("db")
}

case class User(id: Option[Int] = None, email: String,
                firstName: Option[String], lastName: Option[String])

case class Role(id: Option[Int] = None, roleName: String,
                description: Option[String] = None)

case class UserRoleXref(userId: Int, roleId:Int)

trait UsersTable { this: Db =>
  import config.driver.api._

  class Users(tag: Tag) extends Table[User](tag, "USERS") {
    // Columns
    def id = column[Int]("USER_ID", O.PrimaryKey, O.AutoInc)
    def email = column[String]("USER_EMAIL", O.Length(512), O.Unique)
    def firstName = column[Option[String]]("USER_FIRST_NAME", O.Length(64))
    def lastName = column[Option[String]]("USER_LAST_NAME", O.Length(64))

    // Indexes
    def emailIndex = index("USER_EMAIL_IDX", email, true)

    // Select
    def * = (id.?, email, firstName, lastName) <> (User.tupled, User.unapply)
  }

  val users = TableQuery[Users]
}

trait RolesTable { this: Db =>
  import config.driver.api._

  class Roles(tag: Tag) extends Table[Role](tag, "ROLES") {
    // Columns
    def id = column[Int]("ROLE_ID", O.PrimaryKey, O.AutoInc)
    def roleName = column[String]("ROLE_NAME", O.Length(512), O.Unique)
    def description = column[Option[String]]("DESCRIPTON", O.Length(64))

    // Indexes
    def roleNameIndex = index("ROLE_NAME_IDX", roleName, true)

    // Select
    def * = (id.?, roleName, description) <> (Role.tupled, Role.unapply)
  }

  val roles = TableQuery[Roles]
}

trait Db {
  val config: DatabaseConfig[JdbcProfile]
  val db: JdbcProfile#Backend#Database = config.db
}

trait UserRoleMappingTable extends UsersTable with RolesTable { this: Db =>
  import config.driver.api._

  class UserRoleMapping(tag: Tag) extends Table[UserRoleXref](tag, "USER_ROLE_XREF") {
    // Columns
    def userId = column[Int]("USER_ID")
    def roleId = column[Int]("ROLE_ID")

    // primary key
    def pk = primaryKey("USER_ROLE_PK", (userId, roleId))
    def userFk = foreignKey("USER_FK", userId, users)(_.id, ForeignKeyAction.Restrict, ForeignKeyAction.Cascade)
    def roleFk = foreignKey("ROLE_FK", roleId, roles)(_.id, ForeignKeyAction.Restrict, ForeignKeyAction.Cascade)

    // Select
    def * = (userId, roleId)<>(UserRoleXref.tupled, UserRoleXref.unapply)
  }

  val userRoleMapping = TableQuery[UserRoleMapping]
}


class UserRepository(val config: DatabaseConfig[JdbcProfile])
  extends Db with UsersTable with RolesTable with UserRoleMappingTable {

  import config.driver.api._

  import scala.concurrent.ExecutionContext.Implicits.global

  // ...
  def init() = db.run(
    DBIOAction.seq(users.schema.create).andThen(
      DBIOAction.seq(roles.schema.create).andThen(
        DBIOAction.seq(userRoleMapping.schema.create)
      )))
  def drop() = db.run(
    DBIOAction.seq(userRoleMapping.schema.drop).andThen(
      DBIOAction.seq(roles.schema.drop).andThen(
        DBIOAction.seq(users.schema.drop)
    )))

//  def insert(user: User) = db.run(users += user)
//  def insert(role: Role) = db.run(roles += role)
  def insert(user: User) = db
    .run(users returning users.map(_.id) += user)
    .map(id => user.copy(id = Some(id)))

  def insert(role: Role) = db
    .run(roles returning roles.map(_.id) += role)
    .map(id => role.copy(id = Some(id)))

  def insert(user: User, role: Role) = db.run(userRoleMapping += UserRoleXref(user.id.get, role.id.get))

  def find(user:User) =
    db.run((for (u <- users if u.id === user.id) yield u).result.headOption)

  def find(role:Role) =
    db.run((for (r <- roles if r.id === role.id) yield r).result.headOption)

  //    def find(id: Int) = db.run(users.filter(_.id === id).result.headOption)

  def findRoles(f: (Users, Roles) => Rep[Boolean]) = ??? //db.run(findRoles.filter(f).result)

  def findUsers = db.run(users.result)

  def findRoles = db.run(roles.result)

//  def findUsers = for {
//      user <- users
//    }  yield user
//
//  def findRoles = for {
//      role <- roles
//    }  yield role
//
  val query = for {
    ((user, _),role) <- users.join(userRoleMapping).on(_.id === _.userId).join(roles).on(_._2.roleId === _.id)
  }  yield (user, role)

  def findRolesForUser(user:User) = db.run(query.filter(t=>{
    t._1.id === user.id
  }).result)

  def update(id: Int, firstName: Option[String], lastName: Option[String]) = {
    val query = for (user <- users if user.id === id)
      yield (user.firstName, user.lastName)
    db.run(query.update(firstName, lastName)) map { _ > 0 }
  }

  def delete(user: User) =
    db.run(users.filter(_.id === user.id).delete) map { _ > 0 }

  def delete(role: Role) =
    db.run(roles.filter(_.id === role.id).delete) map { _ > 0 }

  def delete(user: User, role: Role) = {
    db.run(userRoleMapping.filter(x=>(x.userId === user.id.get) && (x.roleId === role.id.get)).delete) map { _ > 0 }
  }


  def stream(implicit materializer: Materializer) = Source
    .fromPublisher(db.stream(users.result.withStatementParameters(fetchSize=10)))
    .to(Sink.fold[Seq[User], User](Seq())(_ :+ _))
    .run()

  def getNames(id: Int) = db.run(
    sql"select user_first_name, user_last_name from users where user_id = #$id"
      .as[(String, String)].headOption)

}

