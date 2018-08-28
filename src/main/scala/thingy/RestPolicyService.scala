package thingy

import java.security.Principal

import org.apache.logging.log4j.scala.Logging

import scala.collection.mutable
import scala.math.Ordered.orderingToOrdered

class RestPolicyService extends PolicyService with Logging {

  implicit val ordering = new Ordering[String](){
    override def compare(x: String, y: String): Int = x.compareTo(y)
  }
  //orderingToOrdered(ordering)

  val tree = mutable.TreeMap[String, PolicyModel]()(ordering)

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
//    val x = tree.find((k:String,v:PolicyModel)=>k.compare(resource)<0).getOrElse(null)
    tree.get(resource).get.permit(resource, action, p)
  }

  override def permittedActions(permission: String, resource: String, p: Set[Principal]): Set[String] = ???
}

case class SimplePolicyModel(grant:Directive) extends PolicyModel {
  override def permit(resource: String, action: String, p: Principal): Boolean = {
    grant.permission.resource == resource && grant.permission.permitsAction(action) && grant.principals.contains(p.getName)
  }

  override def test(p: Principal): Boolean = ???

  override def permittedActions(resource: String, p: Set[Principal]): Set[String] = ???
}
