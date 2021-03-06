package thingy

import java.security.{AccessControlException, PrivilegedAction, _}
import java.util.Collections

import javax.security.auth.Subject
import javax.security.auth.login.{AppConfigurationEntry, Configuration, LoginContext, LoginException}
import org.apache.logging.log4j.scala.Logging
import org.scalatest._

import scala.util.{Failure, Success, Try}

class TestSpec extends FlatSpec with Matchers with Logging {

  HierarchicalResource.ROOT.permitsActions("*").byPrincipal("me")
  val A = HierarchicalResource.ROOT.resource("A")
    A.permitsActions("read", "write").byPrincipal("fred")
    A.resource("A1").resource("A2").permitsActions("read", "write", "execute").byPrincipal("barney", "bambam")
  val B = HierarchicalResource.ROOT.resource("B")
    B.permitsActions("read").byPrincipal("wilma")
    B.resource("B1").permitsActions("poke").byPrincipal("roger")
  val C = HierarchicalResource.ROOT.resource("C")
    C.permitsActions("read").byPrincipal("wilma")
    C.resource("C1").resource("C2").resource("C3").permitsActions("read", "write", "execute").byPrincipal("boris")

//  HierarchicalResource.ROOT.permitsActions("*").byPrincipal("me")
//  HierarchicalResource.ROOT.resource("A").permitsActions("read", "write").byPrincipal("fred")
//    .resource("A1")
//    .resource("A2").permitsActions("read", "write", "execute").byPrincipal("barney", "bambam")
//  HierarchicalResource.ROOT.resource("B").permitsActions("read").byPrincipal("wilma")
//    .resource("B1").permitsActions("poke").byPrincipal("roger")
//  HierarchicalResource.ROOT.resource("C").permitsActions("read").byPrincipal("wilma")
//    .resource("C1")
//    .resource("C2")
//    .resource("C3").permitsActions("read", "write", "execute").byPrincipal("boris")
//

  val policy = new Policy() {
    override def implies(domain: ProtectionDomain, permission: java.security.Permission): Boolean = {
      permission match {
        case p:MyPermission => {
          domain.getPrincipals.find(d => p.model.test(d)).isDefined
        }
        case _ => super.implies(domain, permission)
      }
    }
  }

  def createPermission(resource: String, action: String):java.security.Permission = {
    createPermission(resource, action, HierarchicalResource.ROOT)
  }

  def createPermission(resource: String, action: String, resourceImpl:HierarchicalResource):java.security.Permission = {
    new MyPermission("thingy", resource, action, resourceImpl.find(resource).withAction(action))
  }

  "Authorisation" should "do more that check for some stupid role..." in {

    Configuration.setConfiguration(new MyConfiguration())
    Policy.setPolicy(policy)


    // seeking permission to 'read' resource 'A/A1/A2'
    var perm = createPermission("A/A1/A2", "read")

    // as role 'barney'
    var role = "barney"
    var subject = authenticate("trader_role", role)
    subject should be (successFor("trader_role", role))
    // the policy above explicitly allows this
    testIt(perm,subject.get) should be (Success(true))

    // as role 'wilma'
    role = "wilma"
    subject = authenticate("trader_role", role)
    subject should be (successFor("trader_role", role))
    // the policy does not have wilma against this resource
    testIt(perm,subject.get) should be (Success(false))

    // as role 'fred' yabbadabbadoo
    role = "fred"
    subject = authenticate("trader_role", role)
    subject should be (successFor("trader_role", role))
    // the policy has fred attached the the resource hierarchy above this resource
    testIt(perm,subject.get) should be (Success(true))

    // however if we change the action fred it trying to perform to execute, this will fail as it is not explicityly allowed
    perm = createPermission("A/A1/A2", "execute")
    testIt(perm,subject.get) should be (Success(false))

    // role 'me' has all actions
    role = "me"
    subject = authenticate(role)
    subject should be (successFor(role))
    testIt(perm,subject.get) should be (Success(true))

    // simple role based authorization
    perm = new MyPermission("role-based", "*", "*", HierarchicalResource.ROOT.find("ROOT").withAction("*"))
    testIt(perm,subject.get) should be (Success(true))

    // as barney
    role = "barney"
    subject = authenticate(role)
    testIt(perm,subject.get) should be (Success(false))
  }

  def successFor(roles: String*):Success[Subject] = {
    val subject = new Subject()
    Success(roles.map(UserPrincipal(_)).foldLeft[Subject](subject:Subject)((s, p) => {
      s.getPrincipals().add(p)
      s
    }))
  }

  def failureFor(perm:java.security.Permission, roles: String*):Failure[Subject] = {
    Failure[Subject](new AccessControlException("access denied "+perm.toString, perm))
  }

  def authenticate(principals:String*):Try[Subject] = {
    val subject = new Subject()

    try {
      val lc = new LoginContext("Sample", subject, MyCallbackHandler(principals.toArray))

      // attempt authentication
      lc.login()


      logger.info("Authentication succeeded!")
      Success(subject)
    } catch {
      case e: LoginException => {
        logger.error(e.getMessage(), e)
        Failure(e)
      }
      case e: SecurityException => {
        logger.error(e.getMessage(), e)
        Failure(e)
      }
    }
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

  class MyConfiguration extends Configuration {
    override def getAppConfigurationEntry(name: String): Array[AppConfigurationEntry] = {
      Array[AppConfigurationEntry](new AppConfigurationEntry("thingy.SampleLoginModule", AppConfigurationEntry.LoginModuleControlFlag.REQUIRED, Collections.emptyMap()))
    }
  }

  case class MyPermission(name:String, resource:String = "*", action:String = "*", model:PolicyModel) extends BasicPermission(name) {

    override def implies(permission: java.security.Permission):Boolean = {
       permission match {
         case p:MyPermission => p.implies(this)
         case _ => false
       }
    }

    override def toString: String = {
      "MyPermission("+resource+", "+action+")"
    }
  }
}

