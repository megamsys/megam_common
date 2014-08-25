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
import Scalaz._
import scalaz.effect.IO
import scalaz.EitherT._
import scalaz.Validation
import scalaz.Validation.FlatMap._
import scalaz.NonEmptyList._
import net.liftweb.json._
import net.liftweb.json.scalaz.JsonScalaz._
import org.megam.common.jsonscalaz._
import org.megam.common.amqp._
import org.megam.common.amqp.serialization.MessagePayLoadSerialization
import Scalaz._

/**
 * @author rajthilak
 *
 */
case class MessagePayLoad(messages: Messages) {

  def toJValue: JValue = {
    import net.liftweb.json.scalaz.JsonScalaz.toJSON
    toJSON(this)(MessagePayLoadSerialization.writer)
  }

  def toJson(prettyPrint: Boolean = false): String = if (prettyPrint) {
    pretty(render(toJValue))
  } else {
    compactRender(toJValue)
  }

}

object MessagePayLoad {

  def fromJValue(jValue: JValue): Result[MessagePayLoad] = {
    import net.liftweb.json.JsonAST.JValue
    fromJSON(jValue)(MessagePayLoadSerialization.reader)

  }

  def fromJson(json: String): Result[MessagePayLoad] = (Validation.fromTryCatchThrowable[JValue, Throwable] {
    parse(json)
  } leftMap { t: Throwable =>
    UncategorizedError(t.getClass.getCanonicalName, t.getMessage, List())
  }).toValidationNel.flatMap { j: JValue => fromJValue(j) }
}


