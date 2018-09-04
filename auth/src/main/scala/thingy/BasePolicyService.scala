package thingy

import java.security.Principal

import org.apache.logging.log4j.scala.Logging

import scala.collection.mutable

class BasePolicyService(factories:Map[String, PolicyService.Factory] = Map.empty[String, PolicyService.Factory], defaultPolicyService:PolicyService.Factory = new RestPolicyService.Factory()) extends PolicyService with Logging {

  val nested:mutable.Map[String, PolicyService] = mutable.Map[String, PolicyService]()

  override def add(grant: Directive) = {
    nested.getOrElse(grant.permission.name, {
      val policyService = create(grant.permission.name);
      nested.put(grant.permission.name, policyService)
      policyService
    }).add(grant)
  }

  override def revoke(grant: Directive) = {
    nested.getOrElse(grant.permission.name, noop(grant.permission.name)).revoke(grant)
  }

  override def permit(t: (String, String, String), p: Principal): Boolean = {
    val(permission, resource, action) = t
    nested.getOrElse(permission, noop(permission)).permit(t, p)
  }

  override def permittedActions(permission: String, resource: String, p: Set[Principal]): Set[String] = {
    nested.getOrElse(permission, noop(permission)).permittedActions(permission, resource, p)
  }

  def create(name: String): PolicyService = {
    factories.getOrElse(name, defaultPolicyService).create()
  }

  def noop(name: String): PolicyService = {
    new PolicyService(){
      override def add(grant: Directive): Unit = {}

      override def revoke(grant: Directive): Unit = {}

      override def permit(t: (String, String, String), p: Principal): Boolean = false

      override def permittedActions(permission: String, resource: String, p: Set[Principal]): Set[String] = Set.empty[String]
    }
  }

}


