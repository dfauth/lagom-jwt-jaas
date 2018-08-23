package thingy
import java.security.Principal

case class HierarchicalPolicyModel(resource:HierarchicalResource = HierarchicalResource.ROOT, var action:String = "*") extends PolicyModel {

  override def test(p: Principal): Boolean = resource.test(action, p)

  def withAction(action: String): PolicyModel = {
    this.action = action
    this
  }

  override def permittedActions(name:String, p: Set[Principal]): Set[String] = {
//    resource.find(name).
    null
  }
}

