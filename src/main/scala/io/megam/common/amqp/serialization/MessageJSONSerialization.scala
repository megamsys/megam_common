/*
** Copyright [2013-2016] [Megam Systems]
**
** https://opensource.org/licenses/MIT
**
*/
package io.megam.common.amqp.serialization
import scalaz._
import Scalaz._
import scalaz.effect.IO
import scalaz.NonEmptyList._
import java.nio.charset.{ Charset }
import net.liftweb.json._
import net.liftweb.json.scalaz.JsonScalaz.JSONR
import net.liftweb.json.scalaz.JsonScalaz.JSONW
import java.lang.NoSuchFieldError
import io.megam.common.enumeration._
import io.megam.common.amqp._
import net.liftweb.json.scalaz.JsonScalaz._

/**
 * @author rajthilak
 *
 */
object MessageJSONSerialization extends SerializationBase[Messages] {

  implicit override val reader = new JSONR[Messages] {
    override def read(json: JValue): Result[Messages] = {
      json match {
        case JArray(jObjectList) => {
          val list = jObjectList.flatMap {
            jValue: JValue =>
              jValue match {
                case JObject(jFieldList) => jFieldList match {
                  case JField(_, JString(id)) :: JField(_, JString(idVal)) :: Nil => List(id -> idVal)
                  //TODO: error here
                  case _ => List[(String, String)]()
                }
                //TODO: error here
                case _ => List[(String, String)]()
              }

          }
          val messages: Messages = Messages(list)
          messages.successNel[Error]
        }
        case j => UnexpectedJSONError(j, classOf[JArray]).failureNel[Messages]
      }
    }
  }

  implicit override val writer = new JSONW[Messages] {

    override def write(h: Messages): JValue = {
      val listv = h match {
        case Some(x) =>
          x.toList
        case None => List[Message]()
      }
      val messageValue = listv.map(_._2).mkString
      JString(messageValue)
    }
  }
}
