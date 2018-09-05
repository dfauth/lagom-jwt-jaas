package thingy.permissions

class RestPermission(resource:String, actions:String) extends BasePermission("rest-permission", resource:String, actions:String) {

  override def toString: String = {
    "RestPermission("+resource+", "+actions+")"
  }
}

