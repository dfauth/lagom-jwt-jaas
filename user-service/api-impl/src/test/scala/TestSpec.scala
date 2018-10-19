
import api.repo.{Role, User, UserRepository}
import org.apache.logging.log4j.scala.Logging
import org.scalatest._
import slick.basic.DatabaseConfig
import slick.jdbc.JdbcProfile

import scala.concurrent.Await
import scala.concurrent.duration._


trait DbConfiguration {
  lazy val config = DatabaseConfig.forConfig[JdbcProfile]("db")
}

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


