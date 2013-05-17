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
import java.nio.charset.{ Charset }
import net.liftweb.json._
import net.liftweb.json.scalaz.JsonScalaz.JSONR
import net.liftweb.json.scalaz.JsonScalaz.JSONW
import java.lang.NoSuchFieldError
import org.megam.common.enumeration._
import net.liftweb.json.scalaz.JsonScalaz._
/**
 * @author rajthilak
 *
 */
object AMQPRequestTypeSerialization extends SerializationBase[AMQPRequestType] {

  implicit override val reader = new JSONR[AMQPRequestType] {
    override def read(jValue: JValue): ValidationNel[Error, AMQPRequestType] = jValue match {
      case JString(s) => s.readEnum[AMQPRequestType].map(_.successNel[Error]) | {
        UncategorizedError("request type", "unknown request type %s".format(s), List()).failNel[AMQPRequestType]
      }
      case j => UnexpectedJSONError(j, classOf[JArray]).failNel[AMQPRequestType]
    }
  }

  implicit override val writer = new JSONW[AMQPRequestType] {
    override def write(t: AMQPRequestType): JValue = JString(t.stringVal)
  }

}