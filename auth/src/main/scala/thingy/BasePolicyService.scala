package thingy

import java.security
import java.security.{Policy, Principal, ProtectionDomain}

import org.apache.logging.log4j.scala.Logging
import thingy.permissions.BasePermission

import scala.collection.mutable

class BasePolicyService(factories:Map[String, PolicyService.Factory] = Map.empty[String, PolicyService.Factory], defaultPolicyService:PolicyService.Factory = new RestPolicyService.Factory()) extends PolicyService with Logging {

  val nestedPolicy = Policy.getPolicy()
  Policy.setPolicy(new Policy(){

    override def implies(domain: ProtectionDomain, permission: security.Permission): Boolean = {
      permission match {
        case p:BasePermission => permit(domain.getPrincipals.toSeq, p)
        case _ => nestedPolicy.implies(domain, permission)
      }
    }
  })

  val nested:mutable.Map[String, PolicyService] = mutable.Map[String, PolicyService]()

  override def add(grant: Directive) = {
    permit(grant.permission.name, s => {
      nested.put(grant.permission.name, s)
      s.add(grant)
    }, create)
  }

  override def revoke(grant: Directive) = {
    permit(grant.permission.name, s => s.revoke(grant))
  }

  override def permit(t: (String, String, String), p: Principal): Boolean = {
    val(permission, resource, action) = t
    permit(permission, s => s.permit(t, p))
  }

  override def permittedActions(permission: String, resource: String, p: Set[Principal]): Set[String] = {
    permit(permission, s => s.permittedActions(permission, resource, p))
  }

  def permit[T](name:String, f: PolicyService => T): T = {
    permit(name, f, builtIn)
  }

  def permit[T](name:String, f: PolicyService => T, ifNone:String => PolicyService): T = {
    f(nested.getOrElse(name, ifNone(name)))
  }

  def permit(principals:Seq[Principal], q: BasePermission): Boolean = {
    principals.find(p => this.permit(q, p)).isDefined
  }

  def permit(q: BasePermission, p: Principal): Boolean = {
    permit(q.getName, s => s.permit(q, p))
  }

  def create(name: String): PolicyService = {
    factories.getOrElse(name, defaultPolicyService).create()
  }

  def noop(name: String): PolicyService = {
    new PolicyService(){
      override def add(grant: Directive): Unit = {
        logger.warn(s"adding directive ${grant} to a noop policy service")
      }

      override def revoke(grant: Directive): Unit = {
        logger.warn(s"revoking directive ${grant} to a noop policy service")
      }

      override def permit(t: (String, String, String), p: Principal): Boolean = {
        logger.warn(s"testing permit ${t} ${p} on a noop policy service => returns false")
        false
      }

      override def permittedActions(permission: String, resource: String, p: Set[Principal]): Set[String] = {
        logger.warn(s"getting permitted actions ${permission} ${resource} ${p} on a noop policy service => return empty set")
        Set.empty[String]
      }

      override def permit(permission: BasePermission, principal: Principal): Boolean = {
        logger.warn(s"testing permit ${permission} ${principal} on a noop policy service => returns false")
        false
      }
    }
  }

  def builtIn(name: String): PolicyService = {
    new PolicyService(){
      override def add(grant: Directive): Unit = ???

      override def revoke(grant: Directive): Unit = ???

      override def permit(t: (String, String, String), p: Principal): Boolean = {
        p match {
          case s:SuperUserRole => {
            logger.warn(s"testing permit ${t} ${p} on a builtIn policy service => returns true")
            true
          }
          case _ => {
            logger.warn(s"testing permit ${t} ${p} on a builtIn policy service => returns false")
            false
          }
        }
      }

      override def permittedActions(permission: String, resource: String, p: Set[Principal]): Set[String] = {
        logger.warn(s"getting permitted actions ${permission} ${resource} ${p} on a builtIn policy service => return empty set")
        Set.empty[String]
      }

      override def permit(permission: BasePermission, principal: Principal): Boolean = {
        principal match {
          case s:SuperUserRole => {
            logger.warn(s"testing permit ${permission} ${principal} on a builtIn policy service => returns true")
            true
          }
          case _ => {
            logger.warn(s"testing permit ${permission} ${principal} on a builtIn policy service => returns false")
            false
          }
        }
      }
    }
  }

}

case class SuperUserRole() extends SystemPrincipal("role", "auth", "superuser")


