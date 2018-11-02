package test

import java.util.function.Consumer

import automat.Automat.given
import automat.Functions.asListOf
import io.restassured.specification.{RequestLogSpecification, ResponseLogSpecification}
import org.apache.logging.log4j.scala.Logging
import org.hamcrest.Matchers.isOneOf
import org.scalatest.{FlatSpec, Matchers}
import test.TestEnvironment.LOCAL
import test.TestIdentity.{SUPERUSER, WATCHERBGYPSY}
import test.TestResource.{INFO, ROLE, USER}
import test.TestRole.TESTROLE
import test.User.Role

class TestSpec extends FlatSpec with Matchers with Logging {

  def asConsumer[T](f: Function[T,_]) = {
    new Consumer[T](){
      override def accept(t: T): Unit = {f(t)}
    };
  }

  "create an account" should "work" in {

    val ctx = given.environment(LOCAL)
      .identity(SUPERUSER)
      .configureAs(Configurations.basicClient)
      .logInstructions(asConsumer((l:RequestLogSpecification) => l.all()),asConsumer((l:ResponseLogSpecification) => l.all()))

      ctx.post(ROLE,
        new Role("testRole","TestRoleDescription") // post
      ) // post
//      .post(USER,
//        new User(firstName = "Watcher",
//          lastName = "BGypsy",
//          email = WATCHERBGYPSY.username,
//          username = WATCHERBGYPSY.username,
//          password = WATCHERBGYPSY.password)
//      ) // post


//    val response = ctx.get(ROLE.queryString("roleName", "testRole"))
    val response = ctx.get(ROLE)
    val roles = asListOf(classOf[Role])(response)
    logger.info("roles: "+roles)
      response.then
      .statusCode(200)
  }

  "create a role" should "work" in {

    val role = new Role(TESTROLE.roleName, TESTROLE.description)

    given.environment(LOCAL).
      //logInstructions(t => t.all(), t => t.all()).

      post(ROLE,role).

      then.
      statusCode(isOneOf[Integer](200, 400))
  }

  "read back all users " should "work" in {

    val body = given.environment(LOCAL).identity(WATCHERBGYPSY).
      configureAs(Configurations.basicClient).
      //logInstructions(t => t.all(), t => t.all()).
      get(USER.queryString("username", WATCHERBGYPSY.username)).getBody.peek()

      logger.info("response: "+body)


//      then().
//      statusCode(200).
//      body("users[0].username", is(WATCHERBGYPSY.username))
  }

  "read back all roles " should "work" in {

    val body = given.environment(LOCAL).identity(WATCHERBGYPSY).
      configureAs(Configurations.basicClient).
      //logInstructions(t => t.all(), t => t.all()).
      get(ROLE).getBody.peek()

      logger.info("response: "+body)


//      then().
//      statusCode(200).
//      body("users[0].username", is(WATCHERBGYPSY.username))
  }

  "authentication " should "work" in {

    val body = given.environment(LOCAL).identity(WATCHERBGYPSY).
      configureAs(Configurations.basicClient).
      //logInstructions(t => t.all(), t => t.all()).
      get(INFO).getBody.peek()

      logger.info("response: "+body)


//      then().
//      statusCode(200).
//      body("users[0].username", is(WATCHERBGYPSY.username))
  }

  case class Credentials(username: String, password: String)

}
