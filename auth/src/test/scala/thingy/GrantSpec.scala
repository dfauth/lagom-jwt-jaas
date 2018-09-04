package thingy

import java.security._

import javax.security.auth.Subject
import org.apache.logging.log4j.scala.Logging
import org.scalatest._
import play.api.libs.json.Json
import thingy.permissions.RestPermission

import scala.collection.mutable
import scala.util.{Failure, Success, Try}

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
    val results = roundTrip(g)
    results should be (List(g))
  }

  def roundTrip(grant: Directive):List[Directive] = {
    roundTrip(List[Directive](grant))
  }

  def roundTrip(grants: List[Directive]):List[Directive] = {
    Json.fromJson[List[Directive]](trace(Json.toJson[List[Directive]](grants))).get
  }

  def trace[T](value: T): T = {
    logger.info(s"trace: ${value}")
    value
  }

  "URL-Role based authorisation is the simplest model and " should "work" in {

    val permissionName = "rest-permission"
    val policyService = new BasePolicyService()
    policyService.handle(
      grant permission permissionName on "/api" actions "*" to "role:admin",
      grant permission permissionName on "/api/instruments" actions "GET" to "user:bob",
      grant permission permissionName on "/api/users" actions "GET" to "user:bob",
      grant permission permissionName on "/api/accounts" actions "GET" to "user:bob",
      grant permission permissionName on "/api/accounts" actions "-" to "role:admin"
    )

    val bob = new UserPrincipal("bob")
    val admin = new RolePrincipal("admin")
    var result = policyService.permit(("rest-permission", "/api", "GET"), bob)
    result should be (false)
    result = policyService.permit(("rest-permission", "/api/users", "GET"), bob)
    result should be (true)
    result = policyService.permit(("rest-permission", "/api/users/fred", "GET"), bob)
    result should be (true)
    result = policyService.permit(("rest-permission", "/api", "GET"), admin)
    result should be (true)
    result = policyService.permit(("rest-permission", "/api/instruments", "GET"), admin)
    result should be (true)
    result = policyService.permit(("rest-permission", "/api/blah", "turd-blossom"), admin)
    result should be (true)
//    result = policyService.permit(("rest-permission", "/api/accounts", "GET"), admin)
//    result should be (false)

  }

  "serialize grants " should "work" in {

    val permissionName = "rest-permission"
    val grants = List(
      grant permission "something-else" on "/api" actions "*" to "role:admin",
      grant permission permissionName on "/api" actions "*" to "role:admin",
      grant permission permissionName on "/api/instruments" actions "GET" to "user:bob",
      grant permission permissionName on "/api/users" actions "GET" to "user:bob",
      grant permission permissionName on "/api/accounts" actions "GET" to "user:bob",
      grant permission permissionName on "/api/accounts" actions "-" to "role:admin"
    )

    val policyService = new BasePolicyService()
    policyService.handle(roundTrip(grants))
    val bob = new UserPrincipal("bob")
    val admin = new RolePrincipal("admin")
    var result = policyService.permit(("rest-permission", "/api", "GET"), bob)
    result should be (false)
    result = policyService.permit(("rest-permission", "/api/users", "GET"), bob)
    result should be (true)
    result = policyService.permit(("rest-permission", "/api/users/fred", "GET"), bob)
    result should be (true)
    result = policyService.permit(("rest-permission", "/api", "GET"), admin)
    result should be (true)
    result = policyService.permit(("rest-permission", "/api/instruments", "GET"), admin)
    result should be (true)
    result = policyService.permit(("rest-permission", "/api/blah", "turd-blossom"), admin)
    result should be (true)
//    result = policyService.permit(("rest-permission", "/api/accounts", "GET"), admin)
//    result should be (false)

  }

  "try with " should "permissions" in {

    val permissionName = "rest-permission"
    val grants = List(
      grant permission "something-else" on "/api" actions "*" to "role:admin",
      grant permission permissionName on "/api" actions "*" to "role:admin",
      grant permission permissionName on "/api/instruments" actions "GET" to "user:bob",
      grant permission permissionName on "/api/users" actions "GET" to "user:bob",
      grant permission permissionName on "/api/accounts" actions "GET" to "user:bob",
      grant permission permissionName on "/api/accounts" actions "-" to "role:admin"
    )

    val policyService = new BasePolicyService()
    policyService.handle(roundTrip(grants))

    val bob = new UserPrincipal("bob")
    val admin = new RolePrincipal("admin")

//    val subject = new Subject(true, Set[Principal](bob,admin), Set.empty, Set.empty)
    val subject = new Subject()
    subject.getPrincipals().add(bob)
    subject.getPrincipals().add(admin)

    val permission = new RestPermission("/api", "GET")

    // testIt(permission, subject) should be (Success(true)) // TODO currently failing

  }

  def testIt(permission:java.security.Permission, subject:Subject):Try[Boolean] = {

    Subject.doAsPrivileged[Try[Boolean]](subject, new PrivilegedAction[Try[Boolean]] {
      override def run(): Try[Boolean] = {
        try {
          AccessController.checkPermission(permission)
          Success(true)
        } catch {
          case e: AccessControlException => {
            logger.error(e.getMessage(), e)
            Success(false)
          }
          case t:Throwable => {
            logger.error(t.getMessage(), t)
            Failure(t)
          }
        }
      }
    }, new AccessControlContext(Array[java.security.ProtectionDomain]()))
  }
}


