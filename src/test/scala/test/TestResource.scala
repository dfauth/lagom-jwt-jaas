package test;

import java.util.{Collections, Optional}
import java.util.function.Function

import automat.MapBuilder.Value
import automat.{MapBuilder, Resource};

object TestResource {
  val AUTH = TestResource("/api/user/login")
  val IDENTITY = TestResource("/api/state/identity")
  val REGISTRATION = TestResource("/api/client/registration")
  val SUBSCRIPTION = TestResource("/api/stream")
}

case class TestResource(uri:String, builder:MapBuilder[String,_] = new Value(Collections.emptyMap())) extends Resource {
  val map = builder.build()

  override def bodyContent[A](f: Function[A, Optional[String]]): String = ???
}


