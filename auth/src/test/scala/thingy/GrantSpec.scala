package thingy

import javax.security.auth.Subject
import org.apache.logging.log4j.scala.Logging
import org.scalatest._
import play.api.libs.json.Json
import thingy.permissions.{BasePermission, RestPermission, authorize}

import scala.util.Success

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

    var permission = new RestPermission("/api", "GET")
    authorize(permission, subject) should be (Success(false))

    permission = new RestPermission("/api/instruments", "GET")
    authorize(permission, subject) should be (Success(true))

    permission = new RestPermission("/api/instruments/123", "GET")
    authorize(permission, subject) should be (Success(true))

    permission = new RestPermission("/api/users", "GET")
    authorize(permission, subject) should be (Success(true))

    permission = new RestPermission("/api/users/123", "GET")
    authorize(permission, subject) should be (Success(true))

    permission = new RestPermission("/api/users", "POST")
    authorize(permission, subject) should be (Success(false))

    permission = new RestPermission("/api/users/123", "POST")
    authorize(permission, subject) should be (Success(false))

    // add admin role
    subject.getPrincipals().add(admin)
    permission = new RestPermission("/api", "GET")
    authorize(permission, subject) should be (Success(true))

    permission = new RestPermission("/api/users", "POST")
    authorize(permission, subject) should be (Success(true))

    permission = new RestPermission("/api/users/123", "POST")
    authorize(permission, subject) should be (Success(true))

    // revoke bobs access to /api/instruments
    policyService.handle(
      revoke permission permissionName on "/api/instruments" actions "GET" from "user:bob"
    )

    // he can still access as admin
    permission = new RestPermission("/api/instruments", "GET")
    authorize(permission, subject) should be (Success(true))

    permission = new RestPermission("/api/instruments/123", "GET")
    authorize(permission, subject) should be (Success(true))

    permission = new RestPermission("/api/users", "GET")
    authorize(permission, subject) should be (Success(true))

    permission = new RestPermission("/api/users/123", "GET")
    authorize(permission, subject) should be (Success(true))

    permission = new RestPermission("/api/users", "POST")
    authorize(permission, subject) should be (Success(true))

    permission = new RestPermission("/api/users/123", "POST")
    authorize(permission, subject) should be (Success(true))

    // now remove admin role
    subject.getPrincipals().remove(admin)

    // and he should no longer have access
    permission = new RestPermission("/api/instruments", "GET")
    authorize(permission, subject) should be (Success(false))

    permission = new RestPermission("/api/instruments/123", "GET")
    authorize(permission, subject) should be (Success(false))

    // the others remain true
    permission = new RestPermission("/api/users", "GET")
    authorize(permission, subject) should be (Success(true))

    permission = new RestPermission("/api/users/123", "GET")
    authorize(permission, subject) should be (Success(true))

    permission = new RestPermission("/api/users", "POST")
    authorize(permission, subject) should be (Success(false))

    permission = new RestPermission("/api/users/123", "POST")
    authorize(permission, subject) should be (Success(false))

  }

  "custom permission logic " should "also be possible" in {

    val permissionName = "custom-permission"

    case class Credit(value:Int)
    class CustomPermission(resource:String, actions:String, credit:Credit=Credit(0)) extends BasePermission("custom-permission", resource:String, actions:String) {
      override def accept():Boolean = {
        credit.value > 0
      }

      override def toString: String = {
        "CustomPermission("+resource+", "+actions+")"
      }
    }

    val grants = List(
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

    val subject = new Subject()
    subject.getPrincipals().add(bob)

    var permission = new CustomPermission("/api", "GET")
    authorize(permission, subject) should be (Success(false))

    permission = new CustomPermission("/api/instruments", "GET")
    authorize(permission, subject) should be (Success(false)) // zero credit

    permission = new CustomPermission("/api/instruments/123", "GET")
    authorize(permission, subject) should be (Success(false)) // zero credit

    permission = new CustomPermission("/api/users", "GET")
    authorize(permission, subject) should be (Success(false)) // zero credit

    permission = new CustomPermission("/api/users/123", "GET")
    authorize(permission, subject) should be (Success(false)) // zero credit

    permission = new CustomPermission("/api/users", "POST")
    authorize(permission, subject) should be (Success(false)) // zero credit

    permission = new CustomPermission("/api/users/123", "POST")
    authorize(permission, subject) should be (Success(false)) // zero credit

    // repeat with non-zero credit
    val credit = Credit(1)
    permission = new CustomPermission("/api", "GET")
    authorize(permission, subject) should be (Success(false)) ///anyway

    permission = new CustomPermission("/api/instruments", "GET", credit)
    authorize(permission, subject) should be (Success(true)) // zero credit

    permission = new CustomPermission("/api/instruments/123", "GET", credit)
    authorize(permission, subject) should be (Success(true)) // zero credit

    permission = new CustomPermission("/api/users", "GET", credit)
    authorize(permission, subject) should be (Success(true)) // zero credit

    permission = new CustomPermission("/api/users/123", "GET", credit)
    authorize(permission, subject) should be (Success(true)) // zero credit

    permission = new CustomPermission("/api/users", "POST", credit)
    authorize(permission, subject) should be (Success(false))

    permission = new CustomPermission("/api/users/123", "POST", credit)
    authorize(permission, subject) should be (Success(false))

  }

  "subject " should "be propagated" in {

    val permissionName = "rest-permission"

    val grants = List(
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

    val subject = new Subject()
    subject.getPrincipals().add(bob)
    subject.getPrincipals().add(admin)

    var permission = new RestPermission("/api", "GET")
    authorize(permission, subject, f) should be (Success(true))
  }

  "a default role superuser " should "exist by default" in {

    val policyService = new BasePolicyService()

    val bob = new UserPrincipal("bob")
    val admin = SuperUserRole()

    val subject = new Subject()
    subject.getPrincipals().add(bob)
    subject.getPrincipals().add(admin)

    var permission = new RestPermission("/api", "GET")
    authorize(permission, subject, ()=>3.69) should be (Success(3.69))
  }

  def f() = {
    var permission = new RestPermission("/api", "GET")
    authorize(permission) should be (Success(true))
  }

}
