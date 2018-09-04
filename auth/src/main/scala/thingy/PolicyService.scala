package thingy

import java.security.Principal

trait PolicyService {

  def handle(grants: Directive*): Any = {
    grants.foreach(handle(_))
  }

  def handle(grants: List[Directive]): Any = {
    grants.foreach(handle(_))
  }

  def handle(grant: Directive): Unit = {
    grant.action(PolicyService.this, grant)
  }


  def add(grant: Directive)
  def revoke(grant: Directive)
  def permit(t:(String, String, String), p: Principal): Boolean
  def permittedActions(permission:String, resource:String, p: Set[Principal]): Set[String]

}

object PolicyService {
  trait Factory {
    def create():PolicyService
  }
}

