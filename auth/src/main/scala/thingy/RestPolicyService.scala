package thingy

import java.security.Principal

import org.apache.logging.log4j.scala.Logging

import scala.collection.mutable

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

  def descend[T](k: String, map:mutable.Map[String, T]):Seq[T] = {
    f(k) match {
      case Some(s1) => findNearestKey(s1, map)
      case None => Seq.empty[T]
    }
  }

  def findNearestKey[T](k: String, map:mutable.Map[String, T]): Seq[T] = {
    if(k == null) {
      Seq.empty[T]
    } else {
      map.get(k) match {
        case Some(s) => {
          descend(k, map) :+ s
        }
        case None => {
          descend(k, map)
        }
      }
    }
  }

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
