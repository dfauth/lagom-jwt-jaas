package thingy

import java.security.{AccessControlException, PrivilegedAction, _}
import java.util.Collections

import javax.security.auth.Subject
import javax.security.auth.login.{AppConfigurationEntry, Configuration, LoginContext, LoginException}
import org.apache.logging.log4j.scala.Logging
import org.scalatest._

import scala.util.{Failure, Success, Try}

class TestSpec extends FlatSpec with Matchers with Logging {

  Resource.ROOT.permitsActions("*").byPrincipal("me")
  Resource.ROOT.resource("A").permitsActions("read", "write").byPrincipal("fred")
    .resource("A1")
    .resource("A2").permitsActions("read", "write", "execute").byPrincipal("barney", "bambam")
  Resource.ROOT.resource("B").permitsActions("read").byPrincipal("wilma")
    .resource("B1").permitsActions("poke").byPrincipal("roger")
  Resource.ROOT.resource("C").permitsActions("read").byPrincipal("wilma")
    .resource("C1")
    .resource("C2")
    .resource("C3").permitsActions("read", "write", "execute").byPrincipal("boris")


  val policy = new Policy() {
    override def implies(domain: ProtectionDomain, permission: Permission): Boolean = {
      permission match {
        case p:MyPermission => {
          domain.getPrincipals.find(d => p.model.test(d)).isDefined
        }
        case _ => super.implies(domain, permission)
      }
    }
  }

    "A State" should "do something" in {

      Configuration.setConfiguration(new MyConfiguration())
      Policy.setPolicy(policy)


      val resource = "A/A1/A2"
      val action = "read"
      val perm = new MyPermission("thingy", resource, action, Resource.ROOT.find(resource).withAction(action))

      var role = "barney"
      var subject = authenticate("trader_role", role)
      subject should be (successFor("trader_role", role))
      testIt(perm,subject.get) should be (Success(true))

      role = "wilma"
      subject = authenticate("trader_role", role)
      subject should be (successFor("trader_role", role))
      testIt(perm,subject.get) should be (Success(false))

      role = "fred"
      subject = authenticate("trader_role", role)
      subject should be (successFor("trader_role", role))
      testIt(perm,subject.get) should be (Success(true))
    }

  def successFor(roles: String*):Success[Subject] = {
    val subject = new Subject()
    Success(roles.map(SimplePrincipal(_)).foldLeft[Subject](subject:Subject)((s, p) => {
      s.getPrincipals().add(p)
      s
    }))
  }

  def failureFor(perm:Permission, roles: String*):Failure[Subject] = {
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

  def testIt(permission:Permission, subject:Subject):Try[Boolean] = {

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

  case class MyPermission(name:String, resource:String = "*", action:String = "*", model:PermissionModel) extends BasicPermission(name) {

    override def implies(permission: Permission):Boolean = {
       permission match {
         case p:MyPermission => p.implies(this)
         case _ => false
       }
    }

  }




}

