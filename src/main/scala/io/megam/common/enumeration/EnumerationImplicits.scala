/*
** Copyright [2013-2016] [Megam Systems]
**
** https://opensource.org/licenses/MIT
**
*/
package io.megam.common
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
