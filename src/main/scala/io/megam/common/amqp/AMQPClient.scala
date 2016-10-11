/*
** Copyright [2013-2016] [Megam Systems]
**
** https://opensource.org/licenses/MIT
**
*/
package io.megam.common.amqp

/**
 * @author ram
 *
 */
import io.megam.common.amqp._
import scalaz._
import Scalaz._
import net.liftweb.json._
import net.liftweb.json.scalaz.JsonScalaz._
import io.megam.common.amqp.request.{PublishRequest, SubscribeRequest}
import io.megam.common.amqp.response.AMQPResponse

trait AMQPClient {
  def subscribe(f: AMQPResponse => ValidationNel[Throwable, Option[String]], key: RoutingKey): SubscribeRequest
  def publish(messageJson: String, key: RoutingKey = ""): PublishRequest
}
