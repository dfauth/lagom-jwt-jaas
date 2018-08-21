package thingy

import java.security.Principal

case class SimplePrincipal(name:String) extends Principal {
  override def getName: String = name
}
