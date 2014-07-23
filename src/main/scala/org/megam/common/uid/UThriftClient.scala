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
package org.megam.common.uid


import org.apache.thrift.TException
import org.apache.thrift.protocol.{TBinaryProtocol, TProtocol}
import org.apache.thrift.transport.{TFramedTransport, TSocket, TTransport, TTransportException}
import org.megam.service.snowflake.gen.Snowflake
import scala.reflect.Manifest
import org.slf4j.LoggerFactory

/**
 * @author ram
 *
 * T is expected to be your thrift-generated Client class. Example: Snowflake.Client
 */
class UThriftClient[T](implicit man: Manifest[T]) {
  def newClient(protocol: TProtocol)(implicit m: Manifest[T]): T = {
    val constructor = m.runtimeClass.
    getConstructor(classOf[TProtocol])
    constructor.newInstance(protocol).asInstanceOf[T]
  }

  private lazy val logger = LoggerFactory.getLogger(getClass)
 
  /**
   * @param soTimeoutMS the Socket timeout for both connect and read.
   */
  def create(hostname: String, port: Int, soTimeoutMS: Int): (TTransport, T) = {
    val socket = new TSocket(hostname, port, soTimeoutMS)
    val transport = new TFramedTransport(socket)
    val protocol: TProtocol  = new TBinaryProtocol(transport)    
    transport.open()
    logger.debug(("creating new TSocket: remote-host = %s remote-port = %d local-port = %d timeout = %d").
        format(hostname, socket.getSocket.getPort, socket.getSocket.getLocalPort, soTimeoutMS))
    (transport, newClient(protocol))
  }
}

