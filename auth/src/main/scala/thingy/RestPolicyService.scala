package thingy

import java.security.Principal

import org.apache.logging.log4j.scala.Logging

import scala.collection.mutable
import thingy.KeyUtils.findNearestKey

class RestPolicyService extends PolicyService with Logging {

  val tree:mutable.Map[String, PolicyModel] = mutable.Map[String, PolicyModel]()

  override def add(grant: Directive) = {
    tree.applyOrElse[String, PolicyModel](grant.permission.resource, r => {
      val model = SimplePolicyModel(grant)
      tree.put(r, model)
      model
    })
  }
  override def revoke(grant: Directive) = {
    tree.remove(grant.permission.resource)
  }

  override def permit(t: (String, String, String), p: Principal): Boolean = {
    val(permission, resource, action) = t
    !findNearestKey[PolicyModel](resource, tree).filter(model => model.permit(resource, action, p)).isEmpty
  }

  override def permittedActions(permission: String, resource: String, p: Set[Principal]): Set[String] = ???

}

object RestPolicyService {
  class Factory extends PolicyService.Factory {
    override def create(): PolicyService = new RestPolicyService()
  }
}

case class SimplePolicyModel(grant:Directive) extends PolicyModel {
  override def permit(resource: String, action: String, p: Principal): Boolean = {
    grant.permission.impliesResource(resource) && grant.permission.permitsAction(action) && grant.principals.contains(p.getName)
  }

  override def test(p: Principal): Boolean = ???

  override def permittedActions(resource: String, p: Set[Principal]): Set[String] = ???
}
