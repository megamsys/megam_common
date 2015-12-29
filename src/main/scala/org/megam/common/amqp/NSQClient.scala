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

import scalaz._
import Scalaz._
import scalaz.effect.IO
import scalaz.EitherT._
import scalaz.Validation
import scalaz.Validation.FlatMap._
import scalaz.NonEmptyList._
import java.util.concurrent.atomic.AtomicInteger
import scala.concurrent.{ ExecutionContext, Future }
import java.util.concurrent.{ ThreadFactory, Executors }
import org.megam.common.amqp._
import scala.collection._
import net.liftweb.json._
import net.liftweb.json.scalaz.JsonScalaz._
import org.walkmod.nsq.NSQProducer
import NSQClient._
import org.megam.common.amqp.request._
import org.megam.common.amqp.response.{ AMQPResponse, AMQPResponseCode }
/**
 * @author ram
 *
 */
class NSQClient(uri: String, topic: String)(implicit val requestContext: ExecutionContext = NSQExecutionContext) extends AMQPClient {

  /**
   * convert uris to an array of NSQ's Address objects
   * If you have failover servers then feed them in the conf file.
   * Regex that splits an uri from amqp://<userid>@hostname:port/vhost to a tuple
   *  (userid, hostname, post, vhost)
   */
  private lazy val uriToRaw: ValidationNel[Throwable, RawURI] = {
    (Validation.fromTryCatchThrowable[org.megam.common.amqp.RawURI, Throwable] {
      val urisSplitter = """(http|https)\:\/\/(.*)\:([0-9]+)""".r
      uri match {
        case urisSplitter(protocol, hostname, port) =>
          RawURI(protocol + "://" + hostname, port)
      }
    } leftMap { t: Throwable => t }).toValidationNel
  }

  private def mkProducer: ValidationNel[Throwable, NSQProducer] = {
    uriToRaw flatMap { raw: RawURI =>
      (Validation.fromTryCatchThrowable[NSQProducer, Throwable] {
        new NSQProducer(RawURI.mk(raw), topic)
      } leftMap { t: Throwable => t }).toValidationNel flatMap { producer: NSQProducer =>
        producer.successNel[Throwable]
      }
    }
  }

  protected def executePublish(messages: Messages): Future[ValidationNel[Throwable, AMQPResponse]] = Future {
    val messageJson = MessagePayLoad(messages).toJson(false)
    mkProducer flatMap { pd: NSQProducer =>
      (Validation.fromTryCatchThrowable[Unit, Throwable] {
        pd.put(messageJson)
      } leftMap { t: Throwable => t }).toValidationNel flatMap { published: Unit =>
        val responseBody = RawBody("Message [%s] publised to [%s] ---> successfully".format(messageJson, topic))
        AMQPResponse(AMQPResponseCode.Ok, responseBody).successNel[Throwable]
      }
    }
  }

  protected def executeSubscribe(f: AMQPResponse => ValidationNel[Throwable, Option[String]], routingKey: RoutingKey): Future[ValidationNel[Throwable, AMQPResponse]] = Future {
    (new java.lang.Error("Not implemented %s".format(topic)).failureNel[AMQPResponse])
  }

  override def publish(m1: Messages, key: org.megam.common.amqp.RoutingKey = "megam"): PublishRequest = PublishRequest(m1) {
    executePublish(m1)
  }

  override def subscribe(f: AMQPResponse => ValidationNel[Throwable, Option[String]], key: RoutingKey): SubscribeRequest = SubscribeRequest(f, key) {
    executeSubscribe(f, key)
  }

}

object NSQClient {

  private val threadNumber = new AtomicInteger(1)
  lazy val nsqThreadPool = Executors.newCachedThreadPool(new ThreadFactory() {
    override def newThread(r: Runnable): Thread = {
      new Thread(r, "megam_nsq-" + threadNumber.getAndIncrement)
    }
  })

  lazy val NSQExecutionContext = ExecutionContext.fromExecutorService(nsqThreadPool)

}
