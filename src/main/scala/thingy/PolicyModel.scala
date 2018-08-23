package thingy

import java.security.Principal

trait PolicyModel {
  def test(p: Principal): Boolean
  def permittedActions(resource:String, p: Set[Principal]): Set[String]
}
