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
//package test
import org.specs2._
import org.specs2.mutable._
import org.specs2.Specification
import org.megam.common.amqp._
import org.specs2.matcher.MatchResult
import scalaz._
import scalaz.Validation._
import Scalaz._
import jsonscalaz._
import net.liftweb.json._
import net.liftweb.json.scalaz.JsonScalaz._
/**
 * @author rajthilak
 *
 */
class SubscribeSpecs extends Specification {

  def is =
    "SubscribeSpecs".title ^ end ^
      """
  RabbitMQClient is an implementation of AMQPClient that connects to a RabbitMQ server
  """ ^ end ^
      "The AMQP Client Should" ^
      "Correctly do a SUB to a queue" ! Subscribe().succeeds ^
      end

  trait TestContext {

    val uris = "amqp://user@localhost:5672/vhost,amqp://rabbitmq@megam.co:5672/vhost"
    val exchange_name = "megam_exchange"
    val queue_name = "megam_queue"
    val routingKey = "megam_key"

    println("Setting up RabbitMQClient")

    val client = new RabbitMQClient(uris, exchange_name, queue_name)

    protected def execute[T](t: AMQPRequest, expectedCode: AMQPResponseCode = AMQPResponseCode.Ok) = {
      println("Executing AMQPRequest")
      val r = t.executeUnsafe // Returns a AMQPResponse

      r.code must beEqualTo(expectedCode)
    }

    /**
     * This is a callback function invoked when an consumer thirsty for a response wants it to be quenched.
     * The response is a either a success or  a failure delivered as scalaz (ValidationNel).
     */
    protected def quenchThirst(h: AMQPResponse) = {

      val result = h.toJson(true) // the response is parsed back

      val res: ValidationNel[Error, String] = result match {
        case respJSON => respJSON.successNel
        case _        => UncategorizedError("request type", "unsupported response type", List()).failNel

      }
      res
    }

  }

  case class Subscribe() extends TestContext {
    println("Run SUB")
    def succeeds = execute(client.subscribe(quenchThirst, routingKey))

  }

}




