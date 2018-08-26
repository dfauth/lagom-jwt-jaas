package thingy

import java.security.Principal

trait PolicyService {

  def handle(grants: Grant*): Any = {
    grants.foreach(g => g.action.apply(PolicyService.this, g))
  }


  def add(grant: Grant)
  def revoke(grant: Grant)
  def permit(t:(String, String, String), p: Principal): Boolean
  def permittedActions(permission:String, resource:String, p: Set[Principal]): Set[String]
}
