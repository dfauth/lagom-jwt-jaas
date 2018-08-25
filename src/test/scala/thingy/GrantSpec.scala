package thingy

import org.apache.logging.log4j.scala.Logging
import org.scalatest._
import play.api.libs.json.Json

class GrantSpec extends FlatSpec with Matchers with Logging {

  "Json serialization" should "work" in {

    var g = Grant(Permission("name1","resource1","action1"), Set("bob","fred"))
    logger.info("grant: "+g)

    var serialized = Json.toJson[Grant](g)

    logger.info("json: "+serialized)

    var result = Json.fromJson[Grant](serialized).get

    logger.info("result: "+result)

    result should be (g)

    g = grant(Permission("name1","resource1","action1")).to(Set("bob", "fred"))
    logger.info("grant: "+g)

    serialized = Json.toJson[Grant](g)

    logger.info("json: "+serialized)

    result = Json.fromJson[Grant](serialized).get

    logger.info("result: "+result)

    result should be (g)

    g = grant(Permission("name1","resource1","action1")).to("bob")
    logger.info("grant: "+g)

    serialized = Json.toJson[Grant](g)

    logger.info("json: "+serialized)

    result = Json.fromJson[Grant](serialized).get

    logger.info("result: "+result)

    result should be (g)

    val perm:Permission = Permission("name2","resource2","action2")

    grant(perm)  to "bob"

    g = grant permission "fred" on "A/A1/A2" actions "read,write" to "bob"
    result = roundTrip(g)
    result should be (g)
  }

  def roundTrip(grant: Grant) = {
    Json.fromJson[Grant](trace(Json.toJson[Grant](grant))).get
  }

  def trace[T](value: T): T = {
    logger.info(s"trace: ${value}")
    value
  }

}


