package thingy
import java.security.Principal

class SimplePermissionModel(resource:Resource, var action:String) extends PermissionModel {

  override def test(p: Principal): Boolean = resource.test(action, p)

  override def withAction(action: String): PermissionModel = {
    this.action = action
    this
  }
}

