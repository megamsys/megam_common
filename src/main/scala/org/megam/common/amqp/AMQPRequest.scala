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
package org.megam.common.amqp

/**
 * @author ram
 *
 */

import scalaz._
import scalaz.Validation._
import Scalaz._
import scalaz.effect.IO
import scalaz.NonEmptyList._
import scalaz.concurrent._
import net.liftweb.json._
import net.liftweb.json.scalaz.JsonScalaz._

trait AMQPRequest {

  def requestType: AMQPRequestType
  def messages: Messages

  /**
   * prepares an IO that represents executing the HTTP request and returning the response
   * @return an IO representing the HTTP request that executes in the calling thread and
   *         returns the resulting HttpResponse
   */
  def prepare: IO[AMQPResponse] = prepareAsync.map(_.get)

  /**
   * prepares an IO that represents a promise that executes the HTTP request and returns the response
   * @return an IO representing the HTTP request that executes in a promise and returns the resulting HttpResponse
   */
  //this needs to be abstract - it is the "root" of the prepare* and execute*Unsafe functions
  def prepareAsync: IO[Promise[AMQPResponse]]

  /**
   * alias for prepare.unsafePerformIO(). executes the HTTP request immediately in the calling thread
   * @return the HttpResponse that was returned from this HTTP request
   */
  def executeUnsafe: AMQPResponse = prepare.unsafePerformIO()

  /**
   * alias for prepareAsync.unsafePerformIO(). executes the HTTP request in a Promise
   * @return a promise representing the HttpResponse that was returned from this HTTP request
   */
  def executeAsyncUnsafe: Promise[AMQPResponse] = prepareAsync.unsafePerformIO()

  def toJValue(implicit client: AMQPClient): JValue = {
    import net.liftweb.json.scalaz.JsonScalaz.toJSON
    val requestSerialization = new AMQPRequestSerialization(client)
    toJSON(this)(requestSerialization.writer)

  }

  def toJson(prettyPrint: Boolean = false)(implicit client: AMQPClient): String = if (prettyPrint) {
    pretty(render(toJValue(client)))
  } else {
    compactRender(toJValue(client))
  }

}

object AMQPRequest {

  def fromJValue(jValue: JValue)(implicit client: AMQPClient): Result[AMQPRequest] = {
    import net.liftweb.json.JsonAST.JValue
    val requestSerialization = new AMQPRequestSerialization(client)
    fromJSON(jValue)(requestSerialization.reader)

  }

  def fromJson(json: String)(implicit client: AMQPClient): Result[AMQPRequest] = (fromTryCatch {
    parse(json)
  } leftMap { t: Throwable =>
    UncategorizedError(t.getClass.getCanonicalName, t.getMessage, List())
  }).toValidationNel.flatMap { j: JValue => fromJValue(j) }
}

trait PublishRequest extends AMQPRequest {
  override val requestType = AMQPRequestType.PUB
  
}

trait SubscribeRequest extends AMQPRequest {
  override val requestType = AMQPRequestType.SUB

}
