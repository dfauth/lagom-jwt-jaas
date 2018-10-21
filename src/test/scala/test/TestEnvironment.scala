package test

import java.net.URI

import automat.{Environment, Resource}

case class TestEnvironment(host:String = "127.0.0.1", port:Int = 9000) extends Environment {

  override def toUri(resource: Resource): URI = toUri("http", resource)

  override def toUri(protocol: String, resource: Resource): URI = {
    new URI(protocol, null, host, port, resource.uri(), null, null)
  }
}

object TestEnvironment {
  val LOCAL = new TestEnvironment()
}
