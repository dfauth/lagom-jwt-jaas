package thingy.permissions

class RestPermission(resource:String, actions:String) extends BasePermission("rest-permission", resource:String, actions:String) {

  override def implies(permission: java.security.Permission):Boolean = {
    permission match {
      case p:RestPermission => p.implies(this)
      case _ => false
    }
  }

  override def toString: String = {
    "RestPermission("+resource+", "+actions+")"
  }
}

