package thingy

import java.security.Principal

import scala.collection.SortedSet

object HierarchicalResource {
   val ROOT = HierarchicalResource("ROOT")
}

case class HierarchicalResource(name:String, var parent:Option[HierarchicalResource] = None, actions:ActionCollection = ActionCollection(), nested:scala.collection.mutable.Map[String, HierarchicalResource] = scala.collection.mutable.Map()) {

  def permitsActions(action:String*): ActionCollection = {
    action.map(Action(_)).foreach(a => actions.add(a))
    actions
  }

  def resource(name:String):HierarchicalResource = {
    if(nested.contains(name)) {
      nested(name)
    } else {
      val r = HierarchicalResource(name, Option(this))
      nested.put(name, r)
      r
    }
  }

  def find(resource:String):HierarchicalPolicyModel = {
    find(SortedSet(resource.split("[/ | \\.]").toList :_*))
  }

  def find(resource:SortedSet[String]):HierarchicalPolicyModel = {
    // root resource
        if(resource.isEmpty) {
          HierarchicalPolicyModel()
        } else if(resource.tail.isEmpty) {
          HierarchicalPolicyModel(this.resource(resource.head))
        } else {
          this.resource(resource.head).find(resource.tail)
        }
  }

  def test(action:String, p:Principal):Boolean = {
    if(actions.matches(action)) {
      if(actions.contains(p)) {
        true
      } else {
        parent.map(r => r.test(action, p)).getOrElse(false)
      }
    } else {
      parent.map(r => r.test(action, p)).getOrElse(false)
    }
//    ((actions.contains("*") || actions.contains(action)) && principals.contains(p)) || parent.map(r => r.test(action, p)).getOrElse(false)
  }
}

