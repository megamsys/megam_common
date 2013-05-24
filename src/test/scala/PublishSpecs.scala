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
//package test
import org.specs2._
import org.specs2.mutable._
import org.specs2.Specification
import org.megam.common.amqp._
import org.specs2.matcher.MatchResult

/**
 * @author rajthilak
 *
 */
class PublishSpecs extends Specification {

  
  def is =
    "PublishSpecs".title ^ end ^
      """
  RabbitMQClient is an implementation of AMQPClient that connects to a RabbitMQ server
  """ ^ end ^
      "The AMQP Client Should" ^
      "Correctly do a PUB to a queue" ! Publish().succeeds ^
      end

      
  trait TestContext {
    
    val uris = "amqp://localhost:5672,amqp://rabbitmq@megam.co:5672"
    val exchange_name = "logs"
    val queue_name = "sampleQueue"
          
    val message1 = Messages("id" -> "test", "name" -> "Common", "header" -> "megam")
    
    println("Setting up RabbitMQClient")
    
    val client = new RabbitMQClient(uris, exchange_name, queue_name)

    protected def execute[T](t: AMQPRequest, expectedCode: AMQPResponseCode = AMQPResponseCode.Ok)(fn: AMQPResponse => MatchResult[T]) = {
      println("Executing AMQPRequest")
      val r = t.executeUnsafe

      r.code must beEqualTo(expectedCode) and fn(r)
    }
    protected def ensureAMQPOk(h: AMQPResponse) = h.code must beEqualTo(AMQPResponseCode.Ok)
  }


  case class Publish() extends TestContext {
    println("Run PUB")
    def succeeds = execute(client.publish(message1, message1))(ensureAMQPOk(_))

  }

}




