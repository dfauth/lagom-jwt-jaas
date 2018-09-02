package thingy

import java.security.Principal

trait PolicyService {

  def handle(grants: Directive*): Any = {
    grants.foreach(g => g.action.apply(PolicyService.this, g))
  }


  def add(grant: Directive)
  def revoke(grant: Directive)
  def permit(t:(String, String, String), p: Principal): Boolean
  def permittedActions(permission:String, resource:String, p: Set[Principal]): Set[String]
}
