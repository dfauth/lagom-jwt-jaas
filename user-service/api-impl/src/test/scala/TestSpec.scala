
import java.util.concurrent.CountDownLatch

import akka.stream.Materializer
import akka.stream.scaladsl.{Sink, Source}
import org.apache.logging.log4j.scala.Logging
import org.scalatest.FlatSpec
import slick.basic.DatabaseConfig
import slick.dbio.DBIOAction
import slick.jdbc.JdbcProfile

import scala.concurrent.{Await, Future}
import scala.concurrent.duration._
import scala.util.{Failure, Success}


class TestSpec extends FlatSpec with DbConfiguration with Logging {

  val timeout = 500.milliseconds
  val repo = new UserRepository(config)

  def before = {
    Await.result(repo.init(), timeout)
  }

  def after = {
    Await.result(repo.drop(), timeout)
  }

  "User" should "be inserted successfully" in {
    import scala.concurrent.ExecutionContext.Implicits.global

    try {
      before

      val user = User(email = "fred@flintstone.com", firstName = Some("Fred"), lastName = Some("Flintstone"))
      val role = Role(roleName = "admin")
      val userFuture = repo.insert(user)
      val roleFuture = repo.insert(role)
      val userRoleFuture = for{
        user <- userFuture
        role <- roleFuture
      } yield {
        repo.findUsers.onComplete {
          case Success(r) => {
            logger.info("query users:"+r)
          }
        }
        repo.findRoles.onComplete {
          case Success(r) => {
            logger.info("query roles:"+r)
          }
        }
        repo.insert(user, role)
      }
//      Future.sequence(List(userFuture, roleFuture)).onComplete {
//        case Success(x) => {
//          logger.info("query users:"+repo.findUsers)
//          logger.info("query roles:"+repo.findRoles)
//        }
//        case Failure(f) => {
//          logger.info("WOOZ:"+f)
//        }
//      }
      val latch = new CountDownLatch(10)
      val myFuture = Future(()=>{
        logger.info("awaiting")
//        latch.await()
        Thread.sleep(10000)
        logger.info("not awaiting")
      })
        userRoleFuture.onComplete {
        case Success(x) => {
          repo.findRoles.onComplete {
            case Success(r) => {
              logger.info("query myRoles: "+r)
              latch.countDown()
            }
          }
        }
        case Failure(y) => {
          logger.info("failed: "+y)
        }
      }

      Thread.sleep(10000)
      Await.ready(myFuture, 20.seconds)
    } finally {
      after
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
  def findRolesForUser() = for {
      ((user, _),role) <- users.join(userRoleMapping).on(_.id === _.userId).join(roles).on(_._2.roleId === _.id)
    }  yield (user, role)

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

