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
class AMQPRequestSerialization(client: AMQPClient) extends SerializationBase[AMQPRequest] {

  import org.megam.common.amqp.MessageJSONSerialization.{ writer => MessageWriter, reader => MessageReader }
  import org.megam.common.amqp.AMQPRequestTypeSerialization.{ writer => AMQPRequestTypeWriter, reader => AMQPRequestTypeReader }

  protected val AMQPRequestTypeKey = "type"
  protected val MessageKey = "id"

  implicit override val writer = new JSONW[AMQPRequest] {
    override def write(req: AMQPRequest): JValue = {
      val baseFields: List[JField] = JField(AMQPRequestTypeKey, toJSON(req.requestType)(AMQPRequestTypeWriter)) ::
        JField(MessageKey, toJSON(req.messages)(MessageWriter)) ::
        Nil

      JObject(baseFields)
    }
  }

  implicit override val reader = new JSONR[AMQPRequest] {
    import org.megam.common.amqp.AMQPRequestType._
    override def read(json: JValue): Result[AMQPRequest] = {

      val typeField = field[AMQPRequestType](AMQPRequestTypeKey)(json)(AMQPRequestTypeReader)
      
      typeField.flatMap { reqType: AMQPRequestType =>
        val msgs1 = field[Messages](MessageKey)(json)(MessageReader)
        val msgs2 = field[Messages](MessageKey)(json)(MessageReader)

        val baseApplicative = msgs1 |@| msgs2
        val res: ValidationNel[Error, AMQPRequest] = reqType match {
          case PUB => baseApplicative(client.publish(_,_))
          case SUB => baseApplicative(client.subscribe(_,_))
          case _   => UncategorizedError("request type", "unsupported request type %s".format(reqType.stringVal), List()).failNel
        }
        res
      }
    }
  }

}