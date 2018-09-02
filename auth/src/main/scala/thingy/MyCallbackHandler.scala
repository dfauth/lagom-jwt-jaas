package thingy

import javax.security.auth.callback.{Callback, CallbackHandler}

case class MyCallbackHandler(principals: Array[String]) extends CallbackHandler {

  override def handle(callbacks: Array[Callback]): Unit = {
    callbacks.foreach(c => {
      c match {
      case t:TestCallback => t.principals(principals)
    }})
  }
}

case class TestCallback(consumer:Array[String] => Unit ) extends Callback {
  def principals(principals: Array[String]): Unit = consumer(principals)

}
