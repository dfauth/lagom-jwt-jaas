package test

import java.util.function.Consumer

import automat.Automat.given
import io.restassured.specification.RequestLogSpecification
import org.apache.logging.log4j.scala.Logging
import org.hamcrest.Matchers.{is, isOneOf}
import org.scalatest.{FlatSpec, Matchers}
import test.TestEnvironment.LOCAL
import test.TestIdentity.WATCHERBGYPSY
import test.TestResource.USER

class TestSpec extends FlatSpec with Matchers with Logging {

  "create an account" should "work" in {

    val user = new User(firstName = "Watcher",
      lastName = "BGypsy",
      email = "watcherbgypsy@gmail.com",
      username = WATCHERBGYPSY.username,
      password = WATCHERBGYPSY.password)

    given.environment(LOCAL).requestLogInstruction(new Consumer[RequestLogSpecification](){
      override def accept(t: RequestLogSpecification): Unit = {
        t.all()
      }
    }).

      post(USER, user).

      then.
      statusCode(isOneOf[Integer](200, 400))
  }

  "Json serialization" should "work" in {

    given.environment(LOCAL).identity(WATCHERBGYPSY).
      configureAs(Configurations.basicClientWithWebSocket).

      get(USER.queryString("username", WATCHERBGYPSY.username)).


      then().
      statusCode(200).
      body("users[0].username", is(WATCHERBGYPSY.username))
  }

}
