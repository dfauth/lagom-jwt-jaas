package thingy

import java.security.Principal

import org.apache.logging.log4j.scala.Logging

import scala.collection.mutable

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
    findNearestKey[PolicyModel](resource) match {
      case Some(model) => model.permit(resource, action, p)
      case _ => false
    }
  }

  override def permittedActions(permission: String, resource: String, p: Set[Principal]): Set[String] = ???

  def f(k: String): Option[String] = {
    val split = k.split("/");
    val buffer = new StringBuilder
    split.take(split.length-1).filter(!_.isEmpty).foldLeft[StringBuilder](buffer)((buffer, s)=>{
      buffer ++= "/"
      buffer ++= s
    })
    buffer.toString().isEmpty match {
      case false => Some(buffer.toString())
      case true => None
    }
  }

  def findNearestKey[T](k: String): Option[T] = {
    if(k == null) {
      None
    } else {
      tree.get(k) match {
        case s:Some[T] => s
        case None => f(k) match {
          case Some(s1) => findNearestKey(s1)
          case None => None
        }
      }
    }
  }

}

case class SimplePolicyModel(grant:Directive) extends PolicyModel {
  override def permit(resource: String, action: String, p: Principal): Boolean = {
    grant.permission.impliesResource(resource) && grant.permission.permitsAction(action) && grant.principals.contains(p.getName)
  }

  override def test(p: Principal): Boolean = ???

  override def permittedActions(resource: String, p: Set[Principal]): Set[String] = ???
}
