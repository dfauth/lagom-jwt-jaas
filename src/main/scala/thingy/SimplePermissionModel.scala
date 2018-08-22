package thingy
import java.security.Principal

case class SimplePermissionModel(resource:Resource = Resource.ROOT, var action:String = "*") extends PermissionModel {

  override def test(p: Principal): Boolean = resource.test(action, p)

  override def withAction(action: String): PermissionModel = {
    this.action = action
    this
  }
}

