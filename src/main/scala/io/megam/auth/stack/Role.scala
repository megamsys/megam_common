package io.megam.auth.stack


sealed trait Role

object Role {

  type Authority = Role

  val ADMIN   = "admin"
  val REGULAR = "regular"

  case object Administrator extends Role
  case object RegularUser extends Role

  def valueOf(value: String): Role = value match {
    case ADMIN     => Administrator
    case REGULAR   => RegularUser
    case _ => throw new IllegalArgumentException()
  }

}
