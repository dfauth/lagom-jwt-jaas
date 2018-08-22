package thingy

import java.security.{PrivilegedAction, _}
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
      val result = testIt(new MyPermission("fred", resource, action, Resource.ROOT.find(resource).withAction(action)),"trader_role", "barney")

      result should be (successFor("trader_role", "barney"))
    }

  def successFor(roles: String*):Success[Subject] = {
    val subject = new Subject()
    Success(roles.map(SimplePrincipal(_)).foldLeft[Subject](subject:Subject)((s, p) => {
      s.getPrincipals().add(p)
      s
    }))
  }

  def testIt(permission:Permission, principals:String*):Try[Subject] = {
    val subject = new Subject()

    try {
      val lc = new LoginContext("Sample", subject, MyCallbackHandler(principals.toArray))

      // attempt authentication
      lc.login()

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

    logger.info("Authentication succeeded!")

    Subject.doAsPrivileged[Try[Subject]](subject, new PrivilegedAction[Try[Subject]] {
      override def run(): Try[Subject] = {
        try {
          AccessController.checkPermission(permission)
          Success(subject)
        } catch {
          case e: AccessControlException => {
            logger.error(e.getMessage(), e)
            Failure(e)
          }
          case t:Throwable => {
            logger.error(t.getMessage(), t)
            Failure(t)
          }
        }
      }
      //        }, AccessController.getContext)
      //        }, new AccessControlContext(Array[ProtectionDomain](new ProtectionDomain(new CodeSource(new URL(""), Array[java.security.cert.Certificate]())), true))
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

