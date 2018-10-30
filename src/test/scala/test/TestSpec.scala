package test

import java.util.function.Consumer

import automat.Automat.given
import io.restassured.specification.{RequestLogSpecification, ResponseLogSpecification}
import org.apache.logging.log4j.scala.Logging
import org.hamcrest.Matchers.isOneOf
import org.scalatest.{FlatSpec, Matchers}
import test.TestEnvironment.LOCAL
import test.TestIdentity.WATCHERBGYPSY
import test.TestResource.{INFO, ROLE, USER}
import test.TestRole.TESTROLE

class TestSpec extends FlatSpec with Matchers with Logging {

  "create an account" should "work" in {

    val user = new User(firstName = "Watcher",
      lastName = "BGypsy",
      email = WATCHERBGYPSY.username,
      username = WATCHERBGYPSY.username,
      password = WATCHERBGYPSY.password)

    given.environment(LOCAL).logInstructions(new Consumer[RequestLogSpecification](){
      override def accept(t: RequestLogSpecification): Unit = {
        t.all()
      }
    },new Consumer[ResponseLogSpecification](){
      override def accept(t: ResponseLogSpecification): Unit = {
        t.all()
      }
    }).

      post(USER, user).

      then.
      statusCode(isOneOf[Integer](200, 400))
  }

  "create a role" should "work" in {

    val role = new Role(roleName = TESTROLE.roleName,
      description = TESTROLE.description)

    given.environment(LOCAL).logInstructions(new Consumer[RequestLogSpecification](){
      override def accept(t: RequestLogSpecification): Unit = {
        t.all()
      }
    },new Consumer[ResponseLogSpecification](){
      override def accept(t: ResponseLogSpecification): Unit = {
        t.all()
      }
    }).

      post(ROLE,role).

      then.
      statusCode(isOneOf[Integer](200, 400))
  }

  "read back all users " should "work" in {

    val body = given.environment(LOCAL).identity(WATCHERBGYPSY).
      configureAs(Configurations.basicClient).logInstructions(new Consumer[RequestLogSpecification](){
      override def accept(t: RequestLogSpecification): Unit = {
        t.all()
      }
    },new Consumer[ResponseLogSpecification](){
      override def accept(t: ResponseLogSpecification): Unit = {
        t.all()
      }
    }).
      get(USER.queryString("username", WATCHERBGYPSY.username)).getBody.peek()

      logger.info("response: "+body)


//      then().
//      statusCode(200).
//      body("users[0].username", is(WATCHERBGYPSY.username))
  }

  "read back all roles " should "work" in {

    val body = given.environment(LOCAL).identity(WATCHERBGYPSY).
      configureAs(Configurations.basicClient).logInstructions(new Consumer[RequestLogSpecification](){
      override def accept(t: RequestLogSpecification): Unit = {
        t.all()
      }
    },new Consumer[ResponseLogSpecification](){
      override def accept(t: ResponseLogSpecification): Unit = {
        t.all()
      }
    }).
      get(ROLE).getBody.peek()

      logger.info("response: "+body)


//      then().
//      statusCode(200).
//      body("users[0].username", is(WATCHERBGYPSY.username))
  }

  "authentication " should "work" in {

    val body = given.environment(LOCAL).identity(WATCHERBGYPSY).
      configureAs(Configurations.basicClient)
      .logInstructions(new Consumer[RequestLogSpecification](){
      override def accept(t: RequestLogSpecification): Unit = {
        t.all()
      }
    },new Consumer[ResponseLogSpecification](){
      override def accept(t: ResponseLogSpecification): Unit = {
        t.body()
      }
    }).
      get(INFO).getBody.peek()

      logger.info("response: "+body)


//      then().
//      statusCode(200).
//      body("users[0].username", is(WATCHERBGYPSY.username))
  }

  case class Credentials(username: String, password: String)

}
