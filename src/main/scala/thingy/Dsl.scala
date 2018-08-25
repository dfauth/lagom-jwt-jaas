package thingy

case class GrantBuilder(permissions:Set[Permission]) extends Toable {
  override def to(principal:String): Grant = {
    to(Set[String](principal))
  }
  def to(principals:Set[String]): Grant = {
    Grant(permissions, principals)
  }
}

object grant {
  def permission(name:String) = PermissionBuilder(name)
  def apply(permission:Permission):GrantBuilder = apply(Set(permission))
  def apply(permissions:Set[Permission]):GrantBuilder = GrantBuilder(permissions)
}

case class PermissionBuilder(name:String) extends Toable {


  var actions:Option[String] = None
  var resource:Option[String] = None

  override def actions(str: String):Toable = {
    actions = Some(str)
    if(resource.isDefined) {
      GrantBuilder(Set(Permission(name, resource.get, actions.get)))
    } else {
      this
    }
  }

  def on(str: String):Toable = {
    resource = Some(str)
    if(actions.isDefined) {
      GrantBuilder(Set(Permission(name, resource.get, actions.get)))
    } else {
      this
    }
  }
}

trait Toable {
  def actions(str: String):Toable = ???

  def to(str: String):Grant = ???
}

