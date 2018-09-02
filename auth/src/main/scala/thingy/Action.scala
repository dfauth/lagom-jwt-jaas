package thingy

import java.security.Principal

import scala.collection.mutable.ListBuffer

case class Action(name:String) {

  def matches(action: String): Boolean = {
    name != "-" && (name == "*" || name == action)
  }
}

case class ActionCollection(actions:scala.collection.mutable.Set[Action] = scala.collection.mutable.Set[Action](), principals:ListBuffer[Principal] = ListBuffer()) {

  def add(a: Action): Unit = {
    actions.add(a)
  }


  def contains(p: Principal): Boolean = {
    principals.contains(p)
  }

  def ifMatches(action: String)(f:ActionCollection=>Unit):Unit = {
    if(matches(action)) {
      null
    }
  }

  def matches(action: String):Boolean = {
    find(action).isDefined
  }

  def find(action: String):Option[Action] = {
    actions.find(_.matches(action))
  }


  def byPrincipal(principal:String*):ActionCollection = {
    principal.map(p => SimplePrincipal(p)).foreach(p => principals += p)
    this
  }

}
