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
import RabbitMQClient._
import java.util.concurrent.atomic.AtomicInteger
import com.rabbitmq.client._
import scala.concurrent.{ ExecutionContext, Future }
import java.util.concurrent.{ ThreadFactory, Executors }
import scala.collection._
import org.megam.common.amqp._
import net.liftweb.json._
import net.liftweb.json.scalaz.JsonScalaz._
import org.megam.common.amqp.request._
import org.megam.common.amqp.response.{ AMQPResponse, AMQPResponseCode }
/**
 * @author ram
 *
 * Scalazified version of the RabbitMQ Java client. This take inspiration from stackmob's newman (ApacheHttpClient) fascade.
 * Uses the IO monad of scalaz, Future of scala to delays the execution until the callee decides.
 * Every invocation of the RabbitMQClient results in creating a connection. There is no connection pooling performed but rather
 * performed by the default impl. of ConnectionFactory. (This is something to-do, and watch for)
 * Two AMQP activities are supported by this client.
 * publish   : this uses the scala.concurrent feature to execute each of the publish operation in its own thread.
 * subscribe : this uses the scala.concurrent feature to execute each of the subscribe operation in its own thread.
 */

class RabbitMQClient(connectionTimeout: Int, maxChannels: Int, exchangeType: String,
  uris: String, exchangeName: String, queueName: String)(implicit val requestContext: ExecutionContext = AMQPExecutionContext) extends AMQPClient {

  def this(uris: String, exchange: String, queue: String) =
    this(DefaultConnectionTimeout, DefaultChannelMax, DefaultExchangeType, uris, exchange, queue)

  /**
   * convert uris to an array of RabbitMQ's Address objects
   * If you have failover servers then feed them in the conf file.
   * Regex that splits an uri from amqp://<userid>@hostname:port/vhost to a tuple
   *  (userid, hostname, post, vhost)
   */
  private lazy val urisToAddress: ValidationNel[Throwable, Array[Address]] = {
    (Validation.fromTryCatchThrowable[Array[org.megam.common.amqp.RawURI], Throwable] {
      val urisSplitter = """(http|ftp|amqp)\:\/\/(.*)\:([0-9]+)\/([a-z]+)""".r
      uris.split(",").map(uri => uri match {
        case urisSplitter(protocol, hostname, port, vhost) =>
          RawURI(hostname, port, vhost)
      })
    } leftMap { t: Throwable => t }).toValidationNel.flatMap { rawURIArray: Array[RawURI] =>
      (rawURIArray.map { rawUri => new Address(rawUri._1, (rawUri._2).toInt)
      }).successNel[Throwable]
    }

  }

  /**
   * Connect to the rabbitmq system using the connection factory.
   * only on success, we proceed further.
   */
  private lazy val connectionIO: ValidationNel[Throwable, Connection] = {
    val res = eitherT[IO, NonEmptyList[Throwable], ValidationNel[Throwable, Connection]] {
      ((for {
        addrArr <- urisToAddress leftMap { t: NonEmptyList[Throwable] => t }
      } yield {
        (Validation.fromTryCatchThrowable[Connection,Throwable] {
          val factory: ConnectionFactory = new ConnectionFactory()
          println("Connecting to " + addrArr.mkString("{", " :: ", "}"))
          val a = factory.newConnection(addrArr)
          println("Connectedd to " + addrArr.mkString("{", " :: ", "}"))
          a
        } leftMap { t: Throwable => t }).toValidationNel flatMap { new_conn: Connection => new_conn.successNel[Throwable] }
      }).disjunction).pure[IO]
    }.run.map(_.validation).unsafePerformIO
    res.getOrElse(new java.lang.Error("connection not established for [%s]".format(uris)).failureNel[Connection])
  }

  /**
   * Connect to the rabbitmq system using the connection factory.
   * only on success, we proceed further.
   */
  private lazy val channelIO: ValidationNel[Throwable, Channel] = {
    val res = eitherT[IO, NonEmptyList[Throwable], ValidationNel[Throwable, Channel]] {
      ((for {
        connection <- connectionIO leftMap { t: NonEmptyList[Throwable] => t }
      } yield {
        (Validation.fromTryCatchThrowable[Channel,Throwable] {
          println("Creating Channel for connection: " + connection)
          connection.createChannel
        } leftMap { t: Throwable => t }).toValidationNel flatMap { new_channel: Channel => new_channel.successNel[Throwable] }
      }).disjunction).pure[IO]
    }.run.map(_.validation).unsafePerformIO
    res.getOrElse(new java.lang.Error("channel for [%s,%s] not created".format(exchangeName, queueName)).failureNel[Channel])
  }

  def mkPublishChannel(routing: RoutingKey): ValidationNel[Throwable, Channel] = {
    val res = eitherT[IO, NonEmptyList[Throwable], ValidationNel[Throwable, Channel]] {
      ((for {
        channel <- channelIO leftMap { t: NonEmptyList[Throwable] => t }
      } yield {
        (Validation.fromTryCatchThrowable[com.rabbitmq.client.AMQP.Exchange.DeclareOk,Throwable] {
          println("mkPublishChannel: exchangeDeclare start.")
          channel.exchangeDeclare(exchangeName, exchangeType, true)
        } leftMap { t: Throwable => t }).toValidationNel flatMap { exchgDeclOK: AMQP.Exchange.DeclareOk =>
          (Validation.fromTryCatchThrowable[com.rabbitmq.client.AMQP.Queue.DeclareOk,Throwable] {
            // Declare a queue named as "queueName", durable : true, exclusive: false (ie. not restricted to this connection),
            //autodelete: false (ie. let the queue remain), and no other arguments.
            println("mkPublishChannel: exchangeDeclared successfully.")
            channel.queueDeclare(queueName, true, false, false, null)
          } leftMap { t: Throwable => t }).toValidationNel flatMap { queueDeclOK: AMQP.Queue.DeclareOk =>
            (Validation.fromTryCatchThrowable[com.rabbitmq.client.AMQP.Queue.BindOk,Throwable] {
              println("mkPublishChannel: queueDeclared successfully.")
              channel.queueBind(queueName, exchangeName, routing)
            } leftMap { t: Throwable => t }).toValidationNel flatMap { queueBindOK: AMQP.Queue.BindOk =>
              channel.successNel[Throwable]
            }
          }
        }
      }).disjunction).pure[IO]
    }.run.map(_.validation).unsafePerformIO
    println("mkPublishChannel :" + res)
    res.getOrElse(new java.lang.Error("publish channel for [%s,%s] not created".format(exchangeName, queueName)).failureNel[Channel])
  }

  private def mkSubscribeChannel(routing: RoutingKey) = {
    channelIO flatMap { channel: Channel =>
      (Validation.fromTryCatchThrowable[com.rabbitmq.client.AMQP.Queue.BindOk,Throwable] {
        channel.queueBind(queueName, exchangeName, routing)
      } leftMap { t: Throwable => t }).toValidationNel flatMap { queueBindOK: AMQP.Queue.BindOk =>
        channel.successNel[Throwable]
      }
    }
  }

  protected def executePublish(messages: Messages, routingKey: RoutingKey): Future[ValidationNel[Throwable, AMQPResponse]] = Future {
    val messageJson = MessagePayLoad(messages).toJson(false)
    mkPublishChannel(routingKey) flatMap { channel: Channel =>
      (Validation.fromTryCatchThrowable[Unit,Throwable] {
        channel.basicPublish(exchangeName, routingKey, null, messageJson.getBytes())
      } leftMap { t: Throwable => t }).toValidationNel flatMap { published: Unit =>
        val responseBody = RawBody("Message [%s] publised to [%s,%s] ---> successfully".format(messageJson, exchangeName, queueName))
        AMQPResponse(AMQPResponseCode.Ok, responseBody).successNel[Throwable]
      }
    }
  }

  /**
   * Also we need to know if channel.basicConsumer is blocking or non blocking.
   *  If its blocking, then the caller will wait for the results
   *  A DefaultConsumer, which takes a fn (F[A] = > Validation[Failure, Success]
   *  This also needs the channel.
   */
  protected def executeSubscribe(f: AMQPResponse => ValidationNel[Throwable, Option[String]], routingKey: RoutingKey): Future[ValidationNel[Throwable, AMQPResponse]] = Future {
    val res = eitherT[IO, NonEmptyList[Throwable], ValidationNel[Throwable, AMQPResponse]] {
      ((for {
        channel <- mkSubscribeChannel(routingKey) leftMap { t: NonEmptyList[Throwable] => t }
      } yield {
        val consumer = new RabbitMQConsumer(channel, f)
        (Validation.fromTryCatchThrowable[String,Throwable] {
          channel.basicConsume(queueName, true, consumer)
        } leftMap { t: Throwable => t }).toValidationNel flatMap { consumerTag: String =>
          val responseBody = RawBody("Consumer [%s] subscribed to [%s,%s] ---> received messages".format(consumerTag, exchangeName, queueName))
          AMQPResponse(AMQPResponseCode.Ok, responseBody).successNel[Throwable]
        }
      }).disjunction).pure[IO]
    }.run.map(_.validation).unsafePerformIO
    res.getOrElse(new java.lang.Error("Subscribing consumer  to [%s,%s] ---> failed".format(exchangeName, queueName)).failureNel[AMQPResponse])

  }

  override def publish(m1: Messages, key: RoutingKey): PublishRequest = PublishRequest(m1, key) {
    executePublish(m1, key)
  }

  /**
   * The subscribe will take a fn, that will get invoked when a message is received from
   * a queue.
   */
  override def subscribe(f: AMQPResponse => ValidationNel[Throwable, Option[String]], key: RoutingKey): SubscribeRequest = SubscribeRequest(f, key) {
    executeSubscribe(f, key)
  }

}

object RabbitMQClient {

  private[RabbitMQClient] val DefaultConnectionTimeout = ConnectionFactory.DEFAULT_CONNECTION_TIMEOUT
  private[RabbitMQClient] val DefaultChannelMax = ConnectionFactory.DEFAULT_CHANNEL_MAX
  private[RabbitMQClient] val DefaultExchangeType = "fanout"

  private val threadNumber = new AtomicInteger(1)
  lazy val amqpThreadPool = Executors.newCachedThreadPool(new ThreadFactory() {

    override def newThread(r: Runnable): Thread = {
      new Thread(r, "megam_amqp-" + threadNumber.getAndIncrement)
    }
  })

  lazy val AMQPExecutionContext = ExecutionContext.fromExecutorService(amqpThreadPool)

}


