/*
** Copyright [2013-2016] [Megam Systems]
**
** https://opensource.org/licenses/MIT
**
*/
//package test

import io.megam.common.amqp._
import io.megam.common.concurrent._
import io.megam.common.amqp.request.AMQPRequest
import io.megam.common.amqp.response._
import org.specs2._
import scalaz._
import Scalaz._
import org.specs2.mutable._
import org.specs2.Specification
import scala.concurrent.duration._
import org.specs2.matcher.MatchResult

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
