/*
** Copyright [2013-2016] [Megam Systems]
**
** https://opensource.org/licenses/MIT
**
*/
package io.megam.common.amqp.request

/**
 * @author ram
 *
 */

import scalaz._
import scalaz.Validation._
import Scalaz._
import scalaz.NonEmptyList._
import scala.concurrent.{ ExecutionContext, Future }
import scala.concurrent.duration._
import net.liftweb.json._
import net.liftweb.json.scalaz.JsonScalaz._
import io.megam.common.amqp._
import io.megam.common.amqp.request.AMQPRequestType._
import io.megam.common.amqp.request.AMQPRequestExecution._
import io.megam.common.amqp.response.AMQPResponse

trait AMQPRequest {

  def messages: String
  def routingKey: RoutingKey
  def requestType: AMQPRequestType

  /**
   * submit this request and return a {{{scala.concurrent.Future}}} that will contain the response
   * @return the Future that represents the running request
   */
  def apply: Future[ValidationNel[Throwable,AMQPResponse]]

  /**
   * execute this request first, and then a series of other requests each after the previous finished
   * @param otherRequests the other requests to execute, in the given order
   * @return a list of request / response pairs. each request will start immediately after the previous response has finished.
   *         the first request will start immediately
   */
  def andThen(otherRequests: List[AMQPRequest])(implicit ctx: ExecutionContext): List[ReqRespFut] = {
    sequencedRequests(this :: otherRequests)
  }

  /**
   * execute this request concurrently with a series of others
   * @param otherRequests the other requests to execute
   * @return a set of all the requests executed (including this one) and the futures representing their responses
   */
  def concurrentlyWith(otherRequests: List[AMQPRequest])(implicit ctx: ExecutionContext): Set[ReqRespFut] = {
    concurrentRequests(this :: otherRequests)
  }

}

trait PublishRequest extends AMQPRequest {
  override val requestType = AMQPRequestType.PUB
}

object PublishRequest {
  def apply(m: String, key: RoutingKey = "megam")(async: => Future[ValidationNel[Throwable, AMQPResponse]]): PublishRequest = new PublishRequest {
    override lazy val messages = m
    override lazy val routingKey = key
    override lazy val apply = async
  }
}

trait SubscribeRequest extends AMQPRequest {
  override val requestType = AMQPRequestType.SUB
}

object SubscribeRequest {
  def apply(f: AMQPResponse => ValidationNel[Throwable, Option[String]],
    key: RoutingKey)(async: => Future[ValidationNel[Throwable, AMQPResponse]]): SubscribeRequest = new SubscribeRequest {
    override lazy val messages = ""
    override lazy val routingKey = key
    override lazy val apply = async
  }
}
