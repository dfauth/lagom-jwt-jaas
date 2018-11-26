package api.permissions

import thingy.permissions.BasePermission

case class UserServicePermission(resource:String = "*", actions:String) extends BasePermission("user-service", resource, actions) {

}
