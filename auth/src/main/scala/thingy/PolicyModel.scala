package thingy

import java.security.Principal

trait PolicyModel {
  def permit(resource: String, action: String, p: Principal): Boolean
  def test(p: Principal): Boolean
  def permittedActions(resource:String, p: Set[Principal]): Set[String]
}
