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
import scalaz.concurrent._
import java.util.concurrent.{ ThreadFactory, Executors }
import RabbitMQClient._
import java.util.concurrent.atomic.AtomicInteger
import com.rabbitmq.client._
import scala.collection._
import org.megam.common.amqp._
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

class RabbitMQClient(connectionTimeout: Int,
  maxChannels: Int, strategy: Strategy, uris: String, exchange: String, queue: String) extends AMQPClient {

  def this(uris: String, exchange: String, queue: String) =
    this(DefaultConnectionTimeout, DefaultChannelMax, Strategy.Executor(amqpThreadPool), uris, exchange, queue)

  /**
   * convert uris to an array of RabbitMQ's Address objects
   * Address(host, port) ...
   * If you have failover servers then feed them in the conf file.
   * As its lazy val, this gets evaluated only once when its first called and the
   * rest of the code just reuses the old evaluation.
   *
   */
  private lazy val urisToAddress: Array[Address] = {
    val urisArray = uris.split(",")

    /**
     *  Regex that splits an uri from amqp://<userid>@hostname:port/vhost to a tuple
     *  (userid, hostname, post, vhost)
     *
     */

    val urisSplitter = """(http|ftp|amqp)\:\/\/([a-z]+)\@(.*)\:([0-9]+)\/([a-z]+)""".r

    /*
     * Splits a single URI of the form  amqp://<userid>@hostname:port/vhost 
     * Returns a tuple (userid, hostname, port, host) => put it in a type class
     */
    def splitURI(uri: String): RawURI = uri match {
      case urisSplitter(protocol, userid, hostname, port, vhost) =>
        RawURI(userid, hostname, port, vhost)
    }

    val rabbitCrudeAddresses = urisArray.map(uri => splitURI(uri))

    rabbitCrudeAddresses.map(rawUri => new Address(rawUri._2, (rawUri._3).toInt))
  }

  /**
   * Connect to the rabbitmq system using the connection factory.
   */
  private val connManager: Connection = {
    val factory: ConnectionFactory = new ConnectionFactory()
    val addrArr: Array[Address] = urisToAddress
    println("Connecting to " + addrArr.mkString("{", " :: ", "}"))
    val cm = factory.newConnection(addrArr)
    println("Connected to " + addrArr.mkString("{", " ", "}"))
    cm
  }

  /**
   *  Create a channel on the connection.
   *  Refer RabbitMQ Java guide for more info :  http://www.rabbitmq.com/api-guide.html#consuming
   *  The type of exchange should be configurable (fanout, direct etc..)
   */
  private val channel: Channel = {
    connManager.createChannel()
    channel.exchangeDeclare(exchange, "fanout", true)
    val queueName = channel.queueDeclare().getQueue()
    channel.queueBind(queueName, exchange, "sampleLog")
    channel
  }

  /**
   *  A DefaultConsumer, which takes a fn (F[A] = > Validation[Failure, Success]
   *  This also needs the channel. Verify it.
   */
  private val defaultConsumer: Consumer = RabbitMQConsumer(channel, f);

  /**
   * This function wraps an function (t => T) into  concurrent scalaz IO using a strategy.
   * The strategy is a threadpooled executors.
   * This mean any IO monad will be threadpooled when executed.
   */
  private def wrapIOPromise[T](t: => T): IO[Promise[T]] = IO(Promise(t)(strategy))

  protected def liftPublishOp(messages: Messages): IO[Promise[AMQPResponse]] = wrapIOPromise {
    val messageJson = MessagePayLoad(messages).toJson(false)
    println("Hurray publishing :" + messageJson)
    /**
     * What is the null ?
     */
    channel.basicPublish(exchange, "sampleMessage", null, messageJson.getBytes())

    val body = RawBody(messageJson)  // Just return the json back, this will logged saying the message was delivered.
    val responseCode = AMQPResponseCode.Ok
    val responseBody = body
    AMQPResponse(responseCode, responseBody)
  }

  /**
   * Also we need to know if channel.basicConsumer is blocking or non blocking.
   *  If its blocking, then the caller will wait for the results
   */
  protected def liftSubscribeOp(messages: Messages): IO[Promise[AMQPResponse]] = wrapIOPromise {
    // use the consumer
    // channel.basicConsume(queue, true, consumer)

    val responseCode = ???
    val responseBody = ???
    AMQPResponse(responseCode, responseBody)

  }

  override def publish(m1: Messages, m2: Messages): PublishRequest = new PublishRequest {
    override val messages = m1
    override def prepareAsync: IO[Promise[AMQPResponse]] = liftPublishOp(m1)
  }

  /**
   * The subscribe will take a fn, that will get invoked when a message is received from
   * a queue.
   */
  override def subscribe(m1: Messages, m2: Messages): SubscribeRequest = new SubscribeRequest {
    override val messages = m1
    override def prepareAsync: IO[Promise[AMQPResponse]] = liftSubscribeOp(m1)

  }

}

object RabbitMQClient {

  private[RabbitMQClient] val DefaultConnectionTimeout = ConnectionFactory.DEFAULT_CONNECTION_TIMEOUT
  private[RabbitMQClient] val DefaultChannelMax = ConnectionFactory.DEFAULT_CHANNEL_MAX

  private val threadNumber = new AtomicInteger(1)
  lazy val amqpThreadPool = Executors.newCachedThreadPool(new ThreadFactory() {

    override def newThread(r: Runnable): Thread = {
      new Thread(r, "megam_amqp-" + threadNumber.getAndIncrement)
    }
  })
}