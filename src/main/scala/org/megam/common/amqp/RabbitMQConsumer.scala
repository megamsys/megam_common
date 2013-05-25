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

import com.rabbitmq.client.{Channel, DefaultConsumer}

/**
 * @author ram
 *
 */
trait RabbitMQConsumer extends DefaultConsumer {

  
  /** Implement handleDelivery def, that takes the required parms.
   *  Wrap the delivered response in AMQPResponse.
   *  Call the fn with AMQP
   **/
  
}

object RabbitMQConsumer {
  
  /** 
   *  An apply function, that takes two parms, Channel and a function F[A] => Validation[Failure,Success]
   *  where  A = AMQPResponse       
   */ 
  

}