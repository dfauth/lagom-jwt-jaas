package thingy

import java.security.Principal

case class UserPrincipal(name:String) extends Principal {
  override def getName: String = "user:"+name
}
case class RolePrincipal(name:String) extends Principal {
  override def getName: String = "role:"+name
}
