package thingy

case class GrantBuilder(permission:Permission) extends Toable {
  override def to(principal:String): Directive = {
    to(Set[String](principal))
  }
  def to(principals:Set[String]): Directive = {
    Directive(permission, principals)
  }
}

object grant {
  def permission(name:String) = PermissionBuilder(name)
  def apply(permission:Permission):GrantBuilder = GrantBuilder(permission)
}

case class PermissionBuilder(name:String) extends Toable {


  var actions:Option[String] = None
  var resource:Option[String] = None

  override def actions(str: String):Toable = {
    actions = Some(str)
    if(resource.isDefined) {
      GrantBuilder(Permission(name, resource.get, actions.get))
    } else {
      this
    }
  }

  def on(str: String):Toable = {
    resource = Some(str)
    if(actions.isDefined) {
      GrantBuilder(Permission(name, resource.get, actions.get))
    } else {
      this
    }
  }
}

trait Toable {
  def actions(str: String):Toable = ???

  def to(str: String):Directive = ???
}

