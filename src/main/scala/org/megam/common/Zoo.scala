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
import com.twitter.common.net.InetSocketAddressHelper
import com.twitter.common.zookeeper.ZooKeeperClient
import com.twitter.conversions.common.quantity._
import com.twitter.conversions.common.zookeeper._
import com.twitter.conversions.time._
import com.twitter.common.zookeeper.{ ZooKeeperClient }
import org.apache.zookeeper.CreateMode
/**
 * A fascade object to twitter's zookeeper client. We'll use this to add a path, update a path,
 * delete a path, and return the value of a path.
 * Option will be provided to update the watchers when a nodestatus changes.
 * @author ram
 *
 */
object Zoo {

  /**
   * Timeout value as loaded from the config file using ConfigFactory.
   */
  val timeout = 2.seconds
  
  /**
   * Location of the ZK server(s), loaded from the config file using ConfigFactory.
   */
  val addresses = new InetSocketAddress("localhost", 2181) :: Nil
  
  
  lazy val zk =  new ZooKeeperClient(timeout.toIntAmount, addresses.asJava)
 /*
  /**
   * This is gonna create a new node on ZooKeeper
   */
  def add(path: String, value: String, createMode: CreateMode) = zk create (path, value.getBytes, createMode)

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
  *
  */
}

sealed trait NodeStatusChange
case object NodeDeleted extends NodeStatusChange
case class NodeUpdated(maybeValue: Option[String]) extends NodeStatusChange