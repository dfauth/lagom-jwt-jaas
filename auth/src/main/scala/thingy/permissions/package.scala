package thingy

import java.security.{AccessControlContext, AccessControlException, AccessController, PrivilegedAction}

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

  def authorize(permission:java.security.Permission):Try[Boolean] = {
    authorize(permission, () => Unit)
  }

  def authorize(permission:java.security.Permission, f:()=>Unit):Try[Boolean] = {
    authorize(permission, getSubject().getOrElse(emptySubject), f)
  }

  def authorize(permission:java.security.Permission, subject:Subject):Try[Boolean] = {
    authorize(permission, subject, ()=>{})
  }

  def authorize(permission:java.security.Permission, subject:Subject, f:()=>Unit):Try[Boolean] = {

    Subject.doAsPrivileged[Try[Boolean]](subject, new PrivilegedAction[Try[Boolean]] {
      override def run(): Try[Boolean] = {
        try {
          AccessController.checkPermission(permission)
          store(subject)
          f()
          Success(true)
        } catch {
          case e: AccessControlException => {
            logger.error(e.getMessage(), e)
            Success(false)
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
}
