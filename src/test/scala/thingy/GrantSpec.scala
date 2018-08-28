package thingy

import org.apache.logging.log4j.scala.Logging
import org.scalatest._
import play.api.libs.json.Json

import scala.collection.mutable

class GrantSpec extends FlatSpec with Matchers with Logging {

  "Json serialization" should "work" in {

    var g = Directive(Permission("name1","resource1","action1"), Set("bob","fred"))
    logger.info("grant: "+g)

    var serialized = Json.toJson[Directive](g)

    logger.info("json: "+serialized)

    var result = Json.fromJson[Directive](serialized).get

    logger.info("result: "+result)

    result should be (g)

    g = grant(Permission("name1","resource1","action1")).to(Set("bob", "fred"))
    logger.info("grant: "+g)

    serialized = Json.toJson[Directive](g)

    logger.info("json: "+serialized)

    result = Json.fromJson[Directive](serialized).get

    logger.info("result: "+result)

    result should be (g)

    g = grant(Permission("name1","resource1","action1")).to("bob")
    logger.info("grant: "+g)

    serialized = Json.toJson[Directive](g)

    logger.info("json: "+serialized)

    result = Json.fromJson[Directive](serialized).get

    logger.info("result: "+result)

    result should be (g)

    val perm:Permission = Permission("name2","resource2","action2")

    grant(perm)  to "bob"

    g = grant permission "fred" on "A/A1/A2" actions "read,write" to "bob"
    result = roundTrip(g)
    result should be (g)
  }

  def roundTrip(grant: Directive) = {
    Json.fromJson[Directive](trace(Json.toJson[Directive](grant))).get
  }

  def trace[T](value: T): T = {
    logger.info(s"trace: ${value}")
    value
  }

  "URL-Role based authorisation is the simplest model and " should "work" in {

    val permissionName = "rest-permission"
    val policyService = new RestPolicyService()
    policyService.handle(
      grant permission permissionName on "/api" actions "*" to "admin",
      grant permission permissionName on "/api/instruments" actions "GET" to "bob",
      grant permission permissionName on "/api/users" actions "GET" to "bob",
      grant permission permissionName on "/api/accounts" actions "GET" to "bob",
      grant permission permissionName on "/api/accounts" actions "-" to "admin"
    )

    val bob = new SimplePrincipal("bob")
    val admin = new SimplePrincipal("admin")
    var result = policyService.permit(("rest-permission", "/api", "GET"), bob)
    result should be (false)
    result = policyService.permit(("rest-permission", "/api/users", "GET"), bob)
    result should be (true)
//    result = policyService.permit(("rest-permission", "/api/users/fred", "GET"), bob)
//    result should be (true)
//    result = policyService.permit(("rest-permission", "/api", "GET"), admin)
//    result should be (true)
//    result = policyService.permit(("rest-permission", "/api/instruments", "GET"), admin)
//    result should be (true)
//    result = policyService.permit(("rest-permission", "/api/blah", "turd-blossom"), admin)
//    result should be (true)
//    result = policyService.permit(("rest-permission", "/api/accounts", "GET"), admin)
//    result should be (false)

  }

  "key ordering" should "work" in {

    val keys = Set("/api", "/api/instrument", "/api/user", "/api/account", "/api/instrument/blah", "/api/account/1", "/api/account/3")

    val tree = mutable.TreeMap[String, String]()
    keys.foreach(k => {
      tree.put(k, k)
    })

    logger.info("keys: "+tree.keys)

    def f(k: String): Option[String] = {
      val split = k.split("/");
      val buffer = new StringBuilder
      split.take(split.length-1).filter(!_.isEmpty).foldLeft[StringBuilder](buffer)((buffer, s)=>{
        buffer ++= "/"
        buffer ++= s
      })
      buffer.toString().isEmpty match {
        case false => Some(buffer.toString())
        case true => None
      }
    }

    def testKey(k: String): Option[String] = {
      if(k == null) {
        None
      } else {
        tree.get(k) match {
          case s:Some[String] => s
          case None => f(k) match {
            case Some(s1) => testKey(s1)
            case None => None
          }
        }
      }
    }

    f("/api/users") should be (Some("/api"))
    f("/api") should be (None)

    testKey("/api/instrument/blah1").get should be ("/api/instrument")
    testKey("/api/blah").get should be ("/api")
    testKey("/api").get should be ("/api")
    testKey("/api/user").get should be ("/api/user")
    testKey("/api/account").get should be ("/api/account")
    testKey("/api/instrument").get should be ("/api/instrument")
    testKey("/api/instrument/blah").get should be ("/api/instrument/blah")
    testKey("/api/instrument/blah1").get should be ("/api/instrument")
    testKey("/api/account/1").get should be ("/api/account/1")
    testKey("/api/account/2").get should be ("/api/account")
    testKey("/api/account/3").get should be ("/api/account/3")
    testKey("/poo") should be (None)
    testKey("poo") should be (None)
    testKey("/") should be (None)
    testKey("") should be (None)
    testKey(null) should be (None)
  }
}


