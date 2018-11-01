package test

import automat.Identity

object TestIdentity {
  val WATCHERBGYPSY = new TestIdentity("watcherbgypsy@gmail.com", "password")
  val SUPERUSER = new TestIdentity("administrator@domain.com", "password")
}

case class TestIdentity private(val username: String, val password: String) extends Identity {
}

object TestRole {
  val TESTROLE = new Role("testRole","description")
}


