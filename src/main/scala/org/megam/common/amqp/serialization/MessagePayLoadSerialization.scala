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
import Scalaz._
import scalaz.effect.IO
import scalaz.NonEmptyList._
import net.liftweb.json._
import net.liftweb.json.scalaz.JsonScalaz._
import net.liftweb.json.scalaz.JsonScalaz.JSONR
import net.liftweb.json.scalaz.JsonScalaz.JSONW
import org.megam.common.amqp._

/**
 * @author rajthilak
 */

object MessagePayLoadSerialization extends SerializationBase[MessagePayLoad] {
  import org.megam.common.amqp.serialization.MessageJSONSerialization.{ writer => MessageWriter, reader => MessageReader }

  implicit override val writer = new JSONW[MessagePayLoad] {
    override def write(h: MessagePayLoad): JValue = {
      val messagesList: List[List[JField]] = h.messages.map {
        messageList: MessageList =>
          (messageList.list.map { message: Message =>
            // message._2 match {
            // case List(x) =>
            //  JObject(JField(message._1, JArray(message._2)) :: Nil)
            // case _  =>
            JField(message._1, JString(message._2)) 
            // }            
          }).toList :: Nil
      } | List[List[JField]]()

      JObject(messagesList.flatten)
    }
  }

  implicit override val reader = new JSONR[MessagePayLoad] {
    override def read(json: JValue): Result[MessagePayLoad] = {
      json match {
        case JArray(jObjectList) => {
          val list = jObjectList.flatMap {
            jValue: JValue =>
              jValue match {
                case JObject(jFieldList) => jFieldList match {
                  case JField(msgName, JString(msgVal)) :: Nil => List(msgName -> msgVal)
                  //TODO: error here
                  case _                                       => List[(String, String)]()
                }
                //TODO: error here
                case _ => List[(String, String)]()
              }
          }
          val messages: Messages = Messages(list)
          MessagePayLoad(messages).successNel[Error]
        }
        case j => UnexpectedJSONError(j, classOf[JArray]).failureNel[MessagePayLoad]
      }
    }
  }
}





















