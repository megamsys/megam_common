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

import com.rabbitmq.client.{ Channel, DefaultConsumer }
import com.rabbitmq.client.Envelope
import com.rabbitmq.client.AMQP
import scalaz._
import Scalaz._
import net.liftweb.json._
import net.liftweb.json.scalaz.JsonScalaz._
/**
 * @author ram
 *
 */
//trait RabbitMQConsumer extends DefaultConsumer {
class RabbitMQConsumer(channel: Channel, f: AMQPResponse => ValidationNel[Error, String]) extends DefaultConsumer(channel) {

  /**
   * Implement handleDelivery def, that takes the required parms.
   *  Wrap the delivered response in AMQPResponse.
   *  Call the fn with AMQP
   */

  override def handleDelivery(consumerTag: String, envelope: Envelope, properties: AMQP.BasicProperties, body: Array[Byte]) = {
    val routingKey = envelope.getRoutingKey()

    val body_text = new String(body, UTF8Charset)
    println("----" + body_text)
    //val contentType = properties.contentType
    val deliveryTag = envelope.getDeliveryTag()
    val validate = f(AMQPResponse(AMQPResponseCode.Ok, RawBody(body_text)))

    // (process the message components here ...)           
    //deliveryTag
  }
}

/*object RabbitMQConsumer {
  
  /** 
   *  An apply function, that takes two parms, Channel and a function F[A] => Validation[Failure,Success]
   *  where  A = AMQPResponse       
   */ 
  
  def apply(channel: Channel)
}*/