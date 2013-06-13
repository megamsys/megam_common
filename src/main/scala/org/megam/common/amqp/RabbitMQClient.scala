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
import scalaz.NonEmptyList._
import scalaz.effect.IO
import scalaz.concurrent._
import java.util.concurrent.{ ThreadFactory, Executors }
import RabbitMQClient._
import java.util.concurrent.atomic.AtomicInteger
import com.rabbitmq.client._
import scala.collection._
import org.megam.common.amqp._
import net.liftweb.json._
import net.liftweb.json.scalaz.JsonScalaz._
/**
 * @author ram
 *
 * Scalazified version of the RabbitMQ Java client. This follows the insipiration from stackmob's newman (ApacheHttpClient) fascade.
 * Uses the IO monad of scalaz and delays the execution until the unsafePerformIO is called.
 * Every invocation of the RabbitMQClient results in creating a connection. There is no connection pooling performed but rather
 * performed by the default impl. of ConnectionFactory. (This is something to-do, and watch for)
 * Two AMQP activities are supported by this client.
 * publish   : this uses the scalaz.concurrent feature to execute each of the publish operation in its own thread.
 * subscribe : this uses the scalaz.concurrent feature to execute each of the subscribe operation in its own thread.
 */

class RabbitMQClient(connectionTimeout: Int, maxChannels: Int, exchangeType: String,
  strategy: Strategy, uris: String, exchangeName: String, queueName: String) extends AMQPClient {

  def this(uris: String, exchange: String, queue: String) =
    this(DefaultConnectionTimeout, DefaultChannelMax, DefaultExchangeType, Strategy.Executor(amqpThreadPool), uris, exchange, queue)

  /**
   * convert uris to an array of RabbitMQ's Address objects
   * If you have failover servers then feed them in the conf file.
   * Regex that splits an uri from amqp://<userid>@hostname:port/vhost to a tuple
   *  (userid, hostname, post, vhost)
   */
  private lazy val urisToAddress: Array[Address] = {
    val urisSplitter = """(http|ftp|amqp)\:\/\/([a-z]+)\@(.*)\:([0-9]+)\/([a-z]+)""".r

    val rabbitCrudeAddresses = uris.split(",").map(uri => uri match {
      case urisSplitter(protocol, userid, hostname, port, vhost) =>
        RawURI(userid, hostname, port, vhost)
    })
    rabbitCrudeAddresses foreach RawURI.show
    rabbitCrudeAddresses.map(rawUri => new Address(rawUri._2, (rawUri._3).toInt))
  }

  /**
   * Connect to the rabbitmq system using the connection factory.
   */
  private lazy val connManager: Connection = {
    val factory: ConnectionFactory = new ConnectionFactory()
    val addrArr: Array[Address] = urisToAddress
    println("Connecting to " + addrArr.mkString("{", " :: ", "}"))
    val cm = factory.newConnection(addrArr)
    println("Connected to " + addrArr.mkString("{", " ", "}"))
    cm
  }

  /**
   * This function wraps an function (t => T) into  concurrent scalaz IO using a strategy.
   * The strategy is a threadpooled executors.
   * This mean any IO monad will be threadpooled when executed.
   */
  private def wrapIOPromise[T](t: => T): IO[Promise[T]] = IO(Promise(t)(strategy))

  protected def liftPublishOp(messages: Messages, routingKey: RoutingKey): IO[Promise[AMQPResponse]] = wrapIOPromise {    
    val messageJson = MessagePayLoad(messages).toJson(false)
    val pubChannel = ChannelQueue(routingKey).forreq(Some(AMQPRequestType.PUB))   

    println("Hurray publishing :" + messageJson)
    /**
     * What is the null ?
     *
     */
    pubChannel.basicPublish(exchangeName, routingKey, null, messageJson.getBytes())

    val body = RawBody(messageJson) // Just return the json back, this will logged saying the message was delivered.
    val responseCode = AMQPResponseCode.Ok
    val responseBody = body
    AMQPResponse(responseCode, responseBody)
  }

  /**
   * Also we need to know if channel.basicConsumer is blocking or non blocking.
   *  If its blocking, then the caller will wait for the results
   *  A DefaultConsumer, which takes a fn (F[A] = > Validation[Failure, Success]
   *  This also needs the channel.
   */
  protected def liftSubscribeOp(f: AMQPResponse => ValidationNel[Error, String], routingKey: RoutingKey): IO[Promise[AMQPResponse]] = wrapIOPromise {
    // use the consumer
    val subChannel = ChannelQueue(routingKey).forreq(None)
    val consumer = new RabbitMQConsumer(subChannel, f)

    subChannel.basicConsume(queueName, true, consumer)

    //val body = 
    val responseCode = AMQPResponseCode.Ok
    val responseBody = RawBody("Message Subcribe was successfully")
    AMQPResponse(responseCode, responseBody)

  }

  override def publish(m1: Messages, key: RoutingKey): PublishRequest = new PublishRequest {
    override val messages = m1
    override def prepareAsync: IO[Promise[AMQPResponse]] = liftPublishOp(m1, key)
  }

  /**
   * The subscribe will take a fn, that will get invoked when a message is received from
   * a queue.
   */
  override def subscribe(f: AMQPResponse => ValidationNel[Error, String], key: RoutingKey): SubscribeRequest = new SubscribeRequest {
    override val messages = None
    override def prepareAsync: IO[Promise[AMQPResponse]] = liftSubscribeOp(f, key)

  }

  case class ChannelQueue(routing: RoutingKey) {

    def channel: Channel = connManager.createChannel()

    /**
     * Declare a queue named as "queueName", durable : true,
     * exclusive: false (ie. not restricted to this connection),
     * autodelete: false (ie. let the queue remain), and no other arguments.
     */
    val queue: ValidationNel[Error, Option[String]] = {
      val que = Some(channel.queueDeclare(queueName, true, false, false, null).getQueue())
      que match {
        case Some(name) => que.successNel
        case _          => UncategorizedError("Queue creation", "Queue %s creation failure".format(queueName), List()).failNel

      }
    }

    /**
     * prepares the channel for the AMQP request.
     */
    def forreq(reqType: Option[AMQPRequestType]): Channel = {
      reqType match {
        case Some(reqType) => { channel.exchangeDeclare(exchangeName, exchangeType, true); queue }
        case None          => channel
      }
      channel.queueBind(queueName, exchangeName, routing)
      channel
    }

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
}


