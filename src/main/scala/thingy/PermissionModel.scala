package thingy

import java.security.Principal

trait PermissionModel {
  def test(p: Principal): Boolean

  def withAction(action: String): PermissionModel
}
