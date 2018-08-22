package thingy

import java.security.Principal

import scala.collection.SortedSet
import scala.collection.mutable.ListBuffer

object Resource {
   val ROOT = Resource("ROOT")
}

case class Resource(name:String, var parent:Option[Resource] = None, principals:ListBuffer[Principal] = ListBuffer(), actions:ListBuffer[String] = ListBuffer(), nested:scala.collection.mutable.Map[String, Resource] = scala.collection.mutable.Map()) {

  def byPrincipal(principal:String*):Resource = {
    principal.map(p => SimplePrincipal(p)).foreach(p => principals += p)
    this
  }

  def permitsActions(action:String*): Resource = {
    action.foreach(a => actions += a)
    this
  }

  def resource(name:String):Resource = {
    if(nested.contains(name)) {
      nested(name)
    } else {
      val r = Resource(name, Option(this))
      nested.put(name, r)
      r
    }
  }

  def find(resource:String):PermissionModel = {
    find(SortedSet(resource.split("[/ | \\.]").toList :_*))
  }

  def find(resource:SortedSet[String]):PermissionModel = {
    // root resource
        if(resource.isEmpty) {
          SimplePermissionModel()
        } else if(resource.tail.isEmpty) {
          SimplePermissionModel(this.resource(resource.head))
        } else {
          this.resource(resource.head).find(resource.tail)
        }
  }

  def test(action:String, p:Principal):Boolean = {
    ((actions.contains("*") || actions.contains(action)) && principals.contains(p)) || parent.map(r => r.test(action, p)).getOrElse(false)
  }
}

