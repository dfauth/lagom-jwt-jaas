
import api.repo.{Role, User, UserRepository}
import org.apache.logging.log4j.scala.Logging
import org.scalatest._
import slick.basic.DatabaseConfig
import slick.jdbc.JdbcProfile

import scala.concurrent.Await
import scala.concurrent.duration._
import scala.util.Success


trait DbConfiguration {
  lazy val config = DatabaseConfig.forConfig[JdbcProfile]("db")
  lazy val db = config.db
}

class TestSpec extends FlatSpec with DbConfiguration with Matchers with Logging {

  val timeout = 500.milliseconds
  val repo = new UserRepository(config.profile, config.db)

  def beforeTest = {
    Await.result(repo.runInit(), timeout) // init creates an boot administrator user and superuser role
  }

  def afterTest = {
    Await.result(repo.runDrop(), timeout)
  }

  "User" should "be inserted successfully" in {
    import scala.concurrent.ExecutionContext.Implicits.global

    try {
      beforeTest

      var user = User(email = "fred@flintstone.com", firstName = Some("Fred"), lastName = Some("Flintstone"), hashedPassword = "password")
      var user1 = User(email = "wilma@flintstone.com", firstName = Some("Wilma"), lastName = Some("Flintstone"), hashedPassword = "password")
      var role = Role(roleName = "admin")
      var role1 = Role(roleName = "role1")
      val userFuture = repo.runInsert(user)
      val user1Future = repo.runInsert(user1)
      val roleFuture = repo.runInsert(role)
      val role1Future = repo.runInsert(role1)
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
        repo.runInsert(u, r)
        repo.runInsert(u1, r1)
      }
      val users = Await.result(userFuture, 20.seconds)
      val allUsers = Await.result(repo.runFindUsers, 20.seconds)
      logger.info("query all users:"+allUsers)
      allUsers.size should be (3)

      val roles = Await.result(roleFuture, 20.seconds)
      val allRoles = Await.result(repo.runFindRoles, 20.seconds)
      logger.info("query all roles:"+allRoles)
      allRoles.size should be (3)

      Await.result(userRoleFuture, 20.seconds)

      val myUserF = repo.find(user)
      myUserF.onComplete {
        case Success(u) => logger.info("myUser: "+u)
      }
      val myRolesF = repo.runFindRolesForUser(user)
      val myRoles = Await.result(myRolesF, 20.seconds)
      logger.info("myRoles:"+myRoles)
      myRoles.size should be (1)

      logger.info("query myRoles: "+myRoles)
    } finally {
      afterTest
    }
  }

  "User with role" should "be inserted successfully" in {
    import scala.concurrent.ExecutionContext.Implicits.global

    try {
      beforeTest

      var user = User(email = "fred@flintstone.com", firstName = Some("Fred"), lastName = Some("Flintstone"), hashedPassword = "password")
      var role = Role(roleName = "admin")
      val userAction = repo.insert(user)
      val roleAction = repo.insert(role)
      val userRoleFuture = for{
        u <- db.run(userAction)
        r <- db.run(roleAction)
        s <- db.run(repo.insert(u,r))
      } yield {
        s
      }
      val s = Await.result(userRoleFuture, 20.seconds)
      logger.info("userRoleFuture completed: "+s)

      val allUsers = Await.result(repo.runFindUsers, 20.seconds)
      logger.info("query all users:"+allUsers)
      allUsers.size should be (2)

      val allRoles = Await.result(repo.runFindRoles, 20.seconds)
      logger.info("query all roles:"+allRoles)
      allRoles.size should be (2)

      val myUserF = repo.runFindByEmail(user.email)
      myUserF.onComplete {
        case Success(u) if u.isDefined => logger.info("myUser: "+u)
      }
      val myUser = Await.result(myUserF, 20.seconds)
      val myRolesF = repo.runFindRolesForUser(myUser.get)
      myRolesF.onComplete {
        case Success(r) => logger.info("myRoles: "+r)
      }

      val myRoles = Await.result(myRolesF, 20.seconds)
      logger.info("myRoles:"+myRoles)
      myRoles.size should be (1)

      logger.info("query myRoles: "+myRoles)
    } finally {
      afterTest
    }
  }

  "User with role chained " should "be inserted successfully" in {
    import scala.concurrent.ExecutionContext.Implicits.global

    try {
      beforeTest

      var user = User(email = "fred@flintstone.com", firstName = Some("Fred"), lastName = Some("Flintstone"), hashedPassword = "password")
      var role = Role(roleName = "admin")
      val userAction = repo.insert(user)
      val roleAction = repo.insert(role)
      val userRoleFuture = for{
        u <- db.run(userAction)
        r <- db.run(roleAction)
        s <- db.run(repo.insert(u,r))
      } yield {
        s
      }
      val s = Await.result(userRoleFuture, 20.seconds)

      val allUsers = Await.result(repo.runFindUsers, 20.seconds)
      allUsers.size should be (2)

      val allRoles = Await.result(repo.runFindRoles, 20.seconds)
      allRoles.size should be (2)

      val myUserF = repo.runFindByEmail(user.email)
      val result = myUserF.map {
        case Some(u) => u
      }.flatMap {
        case u:User => repo.runFindRolesForUser(u)
      }
      result.onComplete {
        case Success(Seq(s)) => logger.info("s: "+s)
      }
      Await.result(result, 20.seconds)
    } finally {
      afterTest
    }
  }

}


