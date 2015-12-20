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
/**
 * @author ram
 *
 */

import org.specs2.Specification
import org.megam.common.amqp.{ AMQPClient, RabbitMQClient }
import org.megam.common.amqp.response.{ AMQPResponse, AMQPResponseCode }
import java.net.URL
import scalaz._
import Scalaz._
import scalaz.Validation._
import org.megam.common.amqp._
import org.megam.common.concurrent._
import scala.concurrent.{ Future }
import scala.concurrent.duration.Duration

trait NSQClientTests { this: Specification =>

  class NSQClientTests {

    private val topic = "test"
    private val message1 = Messages("message" ->  "{\"Id\":\"APR416511659171905536\"},{\"Action\":\"nstop\"},{\"Args\":\"Nah\"}")

    private def executeP(client: AMQPClient, expectedCode: AMQPResponseCode = AMQPResponseCode.Ok,
      duration: Duration = duration) = {
      import org.megam.common.concurrent.SequentialExecutionContext
      val responseFuture: Future[ValidationNel[Throwable, AMQPResponse]] =
        client.publish(message1).apply
      responseFuture.block(duration).toEither must beRight.like {
        case ampq_res => ampq_res.code must beEqualTo(expectedCode)
      }
    }

    def pubBADURL = {
      lazy val bad_uri = "localhost:4161"
      lazy val badClient = new NSQClient(bad_uri, "test")
      executeP(badClient)
    }

    def pubCONNDOWN = {
      lazy val badconn_uri = "http://localhost:4166"
      lazy val conndownClient = new NSQClient(badconn_uri, "test")
      executeP(conndownClient)
    }

    def pub = {
      lazy val good_uri = "http://localhost:4151"
      lazy val goodClient = new NSQClient(good_uri, "test")
      executeP(goodClient)
    }

  }

  object NSQClientTests {
    def apply = new NSQClientTests()
  }

}
