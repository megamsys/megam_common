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
package org.megam.common.amqp

import scalaz._
import scalaz.effect.IO
import scalaz.NonEmptyList._
import Scalaz._
import net.liftweb.json._
import net.liftweb.json.scalaz.JsonScalaz._
import net.liftweb.json.scalaz.JsonScalaz.JSONR
import net.liftweb.json.scalaz.JsonScalaz.JSONW
/**
 * @author rajthilak
 *
 */

object MessagePayLoadSerialization extends SerializationBase[MessagePayLoad] {
  protected val MessageKey = "message"
  import org.megam.common.amqp.MessageJSONSerialization.{ writer => MessageWriter, reader => MessageReader }

  override implicit val writer = new JSONW[MessagePayLoad] {
    override def write(h: MessagePayLoad): JValue = {      
      JObject(          
        JField(MessageKey, toJSON(h.messages)(MessageWriter)) :: Nil)
    }
  }

  override implicit val reader = new JSONR[MessagePayLoad] {
    override def read(json: JValue): Result[MessagePayLoad] = {
      val msgs1 = field[Messages](MessageKey)(json)(MessageReader)
      msgs1.flatMap { reqType: Messages =>
        val res: ValidationNel[Error, MessagePayLoad] = reqType match {
          case Some(x) => MessagePayLoad(x.some).successNel
          case _       => UncategorizedError("request type", "unsupported request type", List()).failNel
        }
        res
      }
    }
  }
}