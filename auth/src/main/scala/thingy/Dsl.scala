package thingy

case class GrantBuilder(permission:Permission, authAction:AuthorizationAction) extends Toable {
  override def to(principal:String): Directive = {
    to(Set[String](principal))
  }
  def to(principals:Set[String]): Directive = {
    Directive(permission, principals, authAction)
  }
}

object grant {
  def permission(name:String) = PermissionBuilder(name, AuthorizationAction.GRANT)
  def apply(permission:Permission):GrantBuilder = GrantBuilder(permission, AuthorizationAction.GRANT)
}

object revoke {
  def permission(name:String) = PermissionBuilder(name, AuthorizationAction.REVOKE)
  def apply(permission:Permission):GrantBuilder = GrantBuilder(permission, AuthorizationAction.REVOKE)
}

case class PermissionBuilder(name:String, authAction:AuthorizationAction) extends Toable {


  var actions:Option[String] = None
  var resource:Option[String] = None

  override def actions(str: String):Toable = {
    actions = Some(str)
    if(resource.isDefined) {
      GrantBuilder(Permission(name, resource.get, actions.get), authAction)
    } else {
      this
    }
  }

  def on(str: String):Toable = {
    resource = Some(str)
    if(actions.isDefined) {
      GrantBuilder(Permission(name, resource.get, actions.get), authAction)
    } else {
      this
    }
  }
}

trait Toable {
  def actions(str: String):Toable = ???

  def from(str: String):Directive = to(str) // syntactic sugar

  def to(str: String):Directive = ???
}

