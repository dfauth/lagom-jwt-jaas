package thingy

import scala.collection.mutable

object KeyUtils {

  def f(k: String): Option[String] = {
    val split = k.split("/");
    val buffer = new StringBuilder
    split.take(split.length-1).filter(!_.isEmpty).foldLeft[StringBuilder](buffer)((buffer, s)=>{
      buffer ++= "/"
      buffer ++= s
    })
    buffer.toString().isEmpty match {
      case false => Some(buffer.toString())
      case true => None
    }
  }

  def descend[T](k: String, map:mutable.Map[String, T]):Seq[T] = {
    f(k) match {
      case Some(s1) => findNearestKey(s1, map)
      case None => Seq.empty[T]
    }
  }

  def findNearestKey[T](k: String, map:mutable.Map[String, T]): Seq[T] = {
    if(k == null) {
      Seq.empty[T]
    } else {
      map.get(k) match {
        case Some(s) => {
          descend(k, map) :+ s
        }
        case None => {
          descend(k, map)
        }
      }
    }
  }

}
