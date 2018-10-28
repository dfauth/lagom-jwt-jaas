package test

import scala.beans.BeanProperty

case class User(@BeanProperty firstName:String,
                @BeanProperty lastName:String,
                @BeanProperty email:String,
                @BeanProperty username:String,
                @BeanProperty password:String) {

}

case class Role(@BeanProperty roleName:String,
                @BeanProperty description:String) {

}
