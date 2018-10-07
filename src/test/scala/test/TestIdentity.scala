package test

import automat.Identity

object TestIdentity {
  val WATCHERBGYPSY = new TestIdentity("watcherbgypsy", "password")
}

class TestIdentity private(val username: String, val password: String) extends Identity {
}


