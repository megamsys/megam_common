/*
** Copyright [2013-2016] [Megam Systems]
**
** https://opensource.org/licenses/MIT
**
*/
package io.megam.common.amqp

import scalaz._
import scalaz.Validation._
import scala.concurrent.duration._
import scala.concurrent._
import io.megam.common.amqp.request._
import io.megam.common.amqp.request.AMQPRequest
import io.megam.common.amqp.response.AMQPResponse

/**
 * @author ram
 *
 */
package object request {

  implicit class RichAMQPRequest(req: AMQPRequest) {
    def block(duration: Duration = 500.milliseconds): ValidationNel[Throwable,AMQPResponse] = {
      Await.result(req.apply, duration)
    }
  }

}
