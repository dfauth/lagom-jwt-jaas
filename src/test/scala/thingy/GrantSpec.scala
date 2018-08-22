package thingy

import org.apache.logging.log4j.scala.Logging
import org.scalatest._
import play.api.libs.json.Json

class GrantSpec extends FlatSpec with Matchers with Logging {

  "Json serialization" should "work" in {

    val grant = Grant(Set("bob","fred"), Set(Permission("name1","resource1","action1"), Permission("name2","resource2","action2")))
    logger.info("grant: "+grant)

    val serialized = Json.toJson[Grant](grant)

    logger.info("json: "+serialized)

    val result = Json.fromJson[Grant](serialized).get

    logger.info("result: "+result)

    result should be (grant)
  }

}

