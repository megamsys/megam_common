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
import scalaz._
import Scalaz._
import org.specs2.mutable._
import org.specs2.Specification
import org.megam.common.amqp._
import org.megam.common.concurrent._
import scala.concurrent.duration._
import org.specs2.matcher.MatchResult
import org.megam.common.amqp.request.AMQPRequest
import org.megam.common.amqp.response._

/**
 * @author rajthilak
 *
 */

class NSQSpecs extends Specification with NSQClientTests {
  def is =
    "NSQSpecs".title ^ end ^
      "NSQClient is an implementation that connects to a NSQ server" ^ end ^
      "PUB with faulty url should work" ! NSQClientTests.apply.pubBADURL ^ end ^
      "PUB with MQ down    should work" ! NSQClientTests.apply.pubCONNDOWN ^ end ^
      "PUB should work" ! NSQClientTests.apply.pub ^ end ^
      end
}
