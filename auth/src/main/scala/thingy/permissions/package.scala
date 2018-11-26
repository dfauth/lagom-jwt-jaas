package thingy

import java.security.{AccessControlContext, AccessControlException, AccessController, PrivilegedAction}

import api.authentication.TokenContent
import com.lightbend.lagom.scaladsl.api.transport.Forbidden
import javax.security.auth.Subject
import org.apache.logging.log4j.scala.Logging

import scala.util.{Failure, Success, Try}

package object permissions extends Logging {

  val emptySubject = new Subject()

  val subjects = new ThreadLocal[Subject]()

  def store(subject:Subject):Unit = {
    subjects.set(subject)
  }

  def removeSubject():Unit = {
    subjects.remove()
  }

  def getSubject():Option[Subject] = {
    Option[Subject](subjects.get())
  }

  def authorize(permission:java.security.Permission):Try[Unit] = {
    authorize(permission, () => {})
  }

  def authorize[T](permission:java.security.Permission, f:()=>T):Try[T] = {
    authorize(permission, getSubject().getOrElse(emptySubject), f)
  }

  def authorize(permission:java.security.Permission, subject:Subject):Try[Unit] = {
    authorize[Unit](permission, subject, ()=>{})
  }

  def authorize[T](permission:java.security.Permission, subject:Subject, f:()=>T):Try[T] = {

    Subject.doAsPrivileged[Try[T]](subject, new PrivilegedAction[Try[T]] {
      override def run(): Try[T] = {
        try {
          AccessController.checkPermission(permission)
          store(subject)
          Success(f())
        } catch {
          case e: AccessControlException => {
            logger.error(e.getMessage(), e)
            Failure(e)
          }
          case t:Throwable => {
            logger.error(t.getMessage(), t)
            Failure(t)
          }
        } finally {
          removeSubject()
        }
      }
    }, new AccessControlContext(Array[java.security.ProtectionDomain]()))
  }

  def authorize[T](permission:java.security.Permission, tokenContent:TokenContent)(codeBloc: => T):T = {
    authorize(permission, tokenContent.toSubject(), ()=>codeBloc) match {
      case Success(r) => r
      case Failure(t) => t match {
        case a:AccessControlException => throw Forbidden(a)
        case t:Throwable => throw t
      }
    }
  }

}
