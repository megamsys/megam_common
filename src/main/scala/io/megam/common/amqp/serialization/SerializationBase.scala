/*
** Copyright [2013-2016] [Megam Systems]
**
** https://opensource.org/licenses/MIT
**
*/
package io.megam.common.amqp.serialization

import net.liftweb.json.scalaz.JsonScalaz._
/**
 * @author rajthilak
 *
 */
trait SerializationBase[T] {
  def writer: JSONW[T]
  def reader: JSONR[T]
}
