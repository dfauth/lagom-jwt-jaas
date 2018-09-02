package thingy

import java.io.IOException
import java.util

import javax.security.auth.Subject
import javax.security.auth.callback.{Callback, CallbackHandler, UnsupportedCallbackException}
import javax.security.auth.spi.LoginModule

class SampleLoginModule extends LoginModule {

  var handler:CallbackHandler = _
  var subject:Subject = _
  var principals:Array[String] = _


  override def initialize(subject: Subject, callbackHandler: CallbackHandler, sharedState: util.Map[String, _], options: util.Map[String, _]): Unit = {
    this.subject = subject
    this.handler = callbackHandler
  }

  override def login(): Boolean = {
    try {
      this.handler.handle(Array[Callback](TestCallback(c => { this.principals = c})))
      true
    } catch {
      case e: IOException =>
        throw new RuntimeException(e)
      case e: UnsupportedCallbackException =>
        throw new RuntimeException(e)
    }
  }

  override def commit(): Boolean = {
    principals.map((p: String) => SimplePrincipal(p)).foreach(p => subject.getPrincipals.add(p))
    true
  }

  override def abort(): Boolean = true

  override def logout(): Boolean = true
}
