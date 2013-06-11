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

import org.apache.thrift.transport.{ TSocket }
import org.apache.thrift.protocol.{ TProtocol, TBinaryProtocol }

import com.twitter.service.snowflake.gen._

/**
 * @author ram
 *
 */
class UID(host: String, port: Int, agent: String) {

  val protocol: TProtocol = {
    val transport = new TSocket(host, port)
    transport.open()
    new TBinaryProtocol(transport)
  }

  private lazy val client = new Snowflake.Client(protocol)

  def get():Long = {
    client.get_id(agent)
  }
}

object UID {

  def apply(host: String, port: Int, agent: String) = new UID(host, port, agent)

}
