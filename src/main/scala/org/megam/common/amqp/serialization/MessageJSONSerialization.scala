/* 
** Copyright [2012] [Megam Systems]
**
** Licensed under the Apache License, Version 2.0 (the "License");
** you may not use this file except in compliance with the License.
** You may obtain a copy of the License at
**
** http://www.apache.org/licenses/LICENSE-2.0
**
** Unless required by applicable law or agreed to in writing, software
** distributed under the License is distributed on an "AS IS" BASIS,
** WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
** See the License for the specific language governing permissions and
** limitations under the License.
*/
package org.megam.common.amqp.serialization
import scalaz._
import scalaz.effect.IO
import scalaz.NonEmptyList._
import Scalaz._
import java.nio.charset.{ Charset }
import net.liftweb.json._
import net.liftweb.json.scalaz.JsonScalaz.JSONR
import net.liftweb.json.scalaz.JsonScalaz.JSONW
import java.lang.NoSuchFieldError
import org.megam.common.enumeration._
import org.megam.common.amqp._
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
        case j => UnexpectedJSONError(j, classOf[JArray]).failNel[Messages]
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
