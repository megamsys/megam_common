/*
** Copyright [2012-2013] [Megam Systems]
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
package org.megam.common
package enumeration

import scalaz._
import scalaz.Validation._
import Scalaz._
import net.liftweb.json._
import net.liftweb.json.scalaz.JsonScalaz._

/**
 * @author ram
 *
 */
trait EnumerationImplicits {

  implicit class RichString(value: String) {
    def readEnum[T <: Enumeration](implicit reader: EnumReader[T]): Option[T] = reader.read(value)
  }

  implicit def enumerationJSON[T <: Enumeration](implicit reader: EnumReader[T], m: Manifest[T]): JSON[T] = new JSON[T] {
    override def write(value: T): JValue = JString(value.stringVal)
    override def read(json: JValue): Result[T] = json match {
      case JString(s) => (fromTryCatchThrowable[T,Throwable](reader.withName(s)).leftMap { _ =>
        UncategorizedError(s, "Invalid %s: %s".format(m.runtimeClass.getSimpleName, s), Nil)
      }).toValidationNel
      case j => UnexpectedJSONError(j, classOf[JString]).failureNel
    }
  }

}
