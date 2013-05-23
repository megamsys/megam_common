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
import com.rabbitmq.client.Channel
import com.typesafe.config._
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
/*class RabbitMQClient(val connectionTimeout: Int = RabbitMQClient.DefaultConnectionTimeout,
  val maxChannels: Int = RabbitMQClient.DefaultChannelMax,
  val strategy: Strategy = Strategy.Executor(amqpThreadPool),
  uris: String, exchange: String, queue: String) extends AMQPClient {*/

class RabbitMQClient(connectionTimeout: Int,
  maxChannels: Int, strategy: Strategy,
  uris: String, exchange: String, queue: String) extends AMQPClient with AMQPRequest{

  def this(uris: String, exchange: String, queue: String) =
    this(RabbitMQClient.DefaultConnectionTimeout, RabbitMQClient.DefaultChannelMax, Strategy.Executor(amqpThreadPool), uris, exchange, queue)

  /**
   * convert uris to an array of RabbitMQ's Address objects
   * Address(host, port) ...
   * If you have failover servers then feed them in the conf file.
   * As its lazy val, this gets evaluated only once when its first called and the
   * rest of the code just reuses the old evaluation.
   *
   */
  private lazy val urisToAddress: Array[Address] = {
    val urlWithPort = uris.split(",")
    var add = Array[Address]()
    def countWords(text: String) = {
      var count = 0
      for (rawWord <- text.split("[,]+")) {
        val word = rawWord.toLowerCase
        count += 1
      }
      count
    }
    val URL = """(http|ftp|amqp)://(.*)|\/([a-z]|[0-9]|@|&|#|/)+""".r

    def splitURL(url: String) = url match {
      case URL(protocol, domain, tld) =>
        println((protocol, domain, tld))
        domain
    }
    /*  println(countWords(uris))  
    for ( i <- 0 to countWords(uris)-1 ) {
      println(urlWithPort(i))
      val domainWithPort = splitURL(urlWithPort(i))
      val splitDomain = domainWithPort.split(":")     
      add = Array(new Address(splitDomain(0), splitDomain(1).toInt))
    }
    println(countWords(uris))    
    println(add)*/
    val domainWithPort = splitURL(urlWithPort(0))
    val splitDomain = domainWithPort.split(":")
    add = Array(new Address(splitDomain(0), splitDomain(1).toInt))
    add
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
   */
  private val channel: Channel = connManager.createChannel()
  channel.exchangeDeclare(exchange, "fanout", true)
  val queueName = channel.queueDeclare().getQueue()
  channel.queueBind(queueName, exchange, "sampleLog")
  /* channel.exchangeDeclare(exchangeName, "direct", true);
	channel.queueDeclare(queueName, true, false, false, null);
	channel.queueBind(queueName, exchangeName, routingKey);
    */

  /**
   * This function wraps an function (t => T) into  concurrent scalaz IO using a strategy.
   * The strategy is a threadpooled executors.
   * This mean any IO monad will be threadpooled when executed.
   */
  //private def wrapIOPromise[T](t: => T): IO[Promise[T]] = IO(Promise(t)(strategy))
  private def wrapIOPromise[T](t: => T): IO[Promise[T]] = IO(Promise(t))
  protected def liftPublishOp(messages: Messages): IO[Promise[AMQPResponse]] = wrapIOPromise {
    println(messages)
    val messageBodyBytes = "hello".getBytes()
    channel.basicPublish(exchange, "sampleLog", null, messageBodyBytes)
    println(toJson(true))
    //messages.foreach { list: NonEmptyList[(String, String)] =>
      //list.foreach { tup: (String, String) =>
        //  if (!tup._1.equalsIgnoreCase(CONTENT_LENGTH)) {
        //httpMessage.addHeader(tup._1, tup._2)
        //  }
      //}
    //}

    /**
     * call the basicPublish and return the results.
     *
     * val rabbitResp = ???
     *
     * Split the results as deemed fit. Fit it in the AMQPResponse as a tuple.
     *  val responseCode = rabbitResponse.getAllHeaders.map(h => (h.getName, h.getValue)).toList
     *  val responseBody = Option(rabbitResponse.getEntity).map(new BufferedHttpEntity(_)).map(EntityUtils.toByteArray(_))
     */
    val responseCode = ???
    val responseBody = ???
    AMQPResponse(responseCode, responseBody)
  }

  protected def liftSubscribeOp(messages: Messages): IO[Promise[AMQPResponse]] = wrapIOPromise {
    messages.foreach { list: NonEmptyList[(String, String)] =>
      list.foreach { tup: (String, String) =>
        //  if (!tup._1.equalsIgnoreCase(CONTENT_LENGTH)) {
        //httpMessage.addHeader(tup._1, tup._2)
        //  }
      }
    }

    /**
     * call the basicPublish and return the results.
     *
     * val rabbitResp = ???
     *
     * Split the results as deemed fit. Fit it in the AMQPResponse as a tuple.
     *   boolean autoAck = false;
     * channel.basicConsume(queueName, autoAck, "myConsumerTag",
     * new DefaultConsumer(channel) {
     * @Override
     * public void handleDelivery(String consumerTag,
     * Envelope envelope,
     * AMQP.BasicProperties properties,
     * byte[] body)
     * throws IOException
     * {
     * String routingKey = envelope.getRoutingKey();
     * String contentType = properties.contentType;
     * long deliveryTag = envelope.getDeliveryTag();
     * // (process the message components here ...)
     * channel.basicAck(deliveryTag, false);
     * }
     * });
     *
     *    * Split the results as deemed fit. Fit it in the AMQPResponse as a tuple.
     *  val responseCode = rabbitResponse.getAllHeaders.map(h => (h.getName, h.getValue)).toList
     *  val responseBody = Option(rabbitResponse.getEntity).map(new BufferedHttpEntity(_)).map(EntityUtils.toByteArray(_))
     */
    val responseCode = ???
    val responseBody = ???
    AMQPResponse(responseCode, responseBody)

  }

  /**
   * byte[] messageBodyBytes = "Hello, world!".getBytes();
   *
   */

  override def publish(m1: Messages, m2: Messages, client: AMQPClient): PublishRequest = new PublishRequest {
    override val messages = m1
    override def prepareAsync: IO[Promise[AMQPResponse]] = liftPublishOp(m1, client)
  }

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