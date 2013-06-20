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
package org.megam.common

import java.net.InetSocketAddress
import scala.collection.JavaConverters._
/*import com.twitter.common.net.InetSocketAddressHelper

import com.twitter.common.zookeeper._
import com.twitter.common.quantity.Amount;
import com.twitter.common.quantity.Time;

import com.twitter.conversions.common.quantity._
import com.twitter.conversions.common.zookeeper._
import com.twitter.conversions.time._
* 
*/
import org.apache.zookeeper.CreateMode

import org.megam.common.Zoo._
/**
 * A fascade object to twitter's zookeeper client. We'll use this to add a path, update a path,
 * delete a path, and return the value of a path.
 * Option will be provided to update the watchers when a nodestatus changes.
 * @author ram
 *
 */
/*class Zoo(connectionTimeout: Amount[Integer, Time] = DefaultConnectionTimeout, uris: String, nodePath: String) {

  /**
   * Location of the ZK server(s), loaded from the config file using ConfigFactory.
   */
  val addresses = new InetSocketAddress(uris, 2181) :: Nil

  lazy val zk = new ZooKeeperClient(connectionTimeout, addresses.asJava)

 lazy val zkMap = com.twitter.common.zookeeper.ZooKeeperMap.create(zk, nodepath, BYTE_ARRAY_VALUES)

  /**
   * This is will create a new node on ZooKeeper
   */
  def add(path: String, data: String, createMode: CreateMode) = {
    zk.get.create(path, date.getBytes(), ACL, createMode)
  }

  /**
   * This is gonna update the value of a node on ZooKeeper
   */
  def set(path: String, value: String) = zk set (path, value.getBytes)

  /**
   * Deletes a node on ZooKeeper
   */
  def delete(path: String) = zk delete path

  /**
   * Gets the value of the node
   */
  def get(path: String) = zk get path

  /**
   * Callback
   */

  def on(path: String)(runnable: NodeStatusChange => Unit) = {
    zk watchNode (path, {
      (data: Option[Array[Byte]]) =>
        data match {
          case Some(d) if d.isEmpty =>
            please log "Node [%s] changed to be empty".format(path)
            runnable(NodeUpdated(None))
          case Some(d) =>
            val value = new String(d)
            please log "Node [%s] updated: %s".format(path, value)
            runnable(NodeUpdated(Option(value)))
          case None =>
            please log "Node [%s] deleted!".format(path)
            runnable(NodeDeleted)
        }
    })
  }
  
  
}

*/

object Zoo {

  /**
   * Timeout value as loaded from the config file using ConfigFactory.
   */
  //private[Zoo] val DefaultConnectionTimeout = 10.seconds.toIntAmount

  private[Zoo] val DefaultParentPath: ZooPath = "/machines"

  //ZooKeeperUtils.ensurePath(zkClient, ACL, parentPath);

 // def apply(uris: String, nodePath: String) = new Zoo(DefaultConnectionTimeout,uris, nodePath)

}

sealed trait NodeStatusChange
case object NodeDeleted extends NodeStatusChange
case class NodeUpdated(maybeValue: Option[String]) extends NodeStatusChange