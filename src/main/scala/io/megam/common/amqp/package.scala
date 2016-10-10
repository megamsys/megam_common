/*
** Copyright [2013-2016] [Megam Systems]
**
** https://opensource.org/licenses/MIT
**
*/
package io.megam.common

import scalaz._
import scalaz.effect.IO
import scalaz.NonEmptyList._
import Scalaz._
import java.nio.charset.{ Charset }
import net.liftweb.json._
import net.liftweb.json.scalaz._
import net.liftweb.json.scalaz.JsonScalaz._
/**
 * @author ram
 *
 */
package object amqp {

  val UTF8Charset = Charset.forName("UTF-8")

  type Message = (String, String)
  type MessageList = NonEmptyList[Message]
  type Messages = Option[MessageList]

  object Messages {
    implicit val MessagesEqual = new Equal[Messages] {
      override def equal(messages1: Messages, messages2: Messages): Boolean = (messages1, messages2) match {
        case (Some(m1), Some(m2)) => m1.list === m2.list
        case (None, None)         => true
        case _                    => false
      }
    }

    implicit val MessagesShow = new Show[Messages] {
      override def shows(h: Messages): String = {
        val s = ~h.map { messageList: MessageList =>
          messageList.list.map(h => h._1 + "=" + h._2).mkString("&")
        }
        s
      }
    }

    def apply(m: Message): Messages = Messages(nels(m))
    def apply(m: Message, tail: Message*): Messages = Messages(nel(m, tail.toList))
    def apply(m: MessageList): Messages = m.some
    def apply(m: List[Message]): Messages = m.toNel
    def empty: Option[MessageList] = Option.empty[MessageList]
  }

  type RawBody = Array[Byte]

  implicit val RawBodyMonoid: Monoid[RawBody] = Monoid.instance(_ ++ _, Array[Byte]())

  object RawBody {
    def apply(s: String, charset: Charset = UTF8Charset): Array[Byte] = s.getBytes(charset)
    def apply(b: Array[Byte]): Array[Byte] = b
    lazy val empty = Array[Byte]()
  }

  type RawURI = (String, String, String, String)

  object RawURI {

    def apply(hostname: String, port: String): RawURI = RawURI("megam", hostname, port, "megam")
    def apply(userid: String = "megam", hostname: String, port: String, vhost: String = "megam"): RawURI = new RawURI(userid, hostname, port, vhost)
    def show(ruri: RawURI) = (ruri._1 + " " + ruri._2 + " " + ruri._3 + " " + ruri._4)
    def mk(ruri: RawURI) = ruri._2 + ":" + ruri._3

  }

  type RoutingKey = String


}
