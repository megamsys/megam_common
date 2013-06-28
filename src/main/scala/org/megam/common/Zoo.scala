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

import scalaz._
import Scalaz._
import scalaz.Validation._
import java.net.InetSocketAddress
import org.apache.zookeeper._
import org.apache.zookeeper.CreateMode
import com.twitter.zk._
import org.megam.common.Zoo._
import com.twitter.util._
import java.util.concurrent.TimeUnit
import java.util.concurrent.{ ScheduledThreadPoolExecutor }
import com.twitter.conversions.time._
import scala.collection.JavaConverters._
import scala.collection.Set
import com.twitter.logging.Logger
import com.twitter.util.{ Duration, Future, Promise, TimeoutException, Timer, Return, Await }
import com.twitter.concurrent.{ Broker, Offer, Serialized }
import java.io.Serializable
import java.util.concurrent.{ Future => JavaFuture, TimeUnit }
import com.twitter.concurrent.Scheduler
import org.apache.zookeeper.ZooDefs.Ids
import org.apache.zookeeper.data.{ ACL, Stat }
import scala.collection.{ Seq, Set }
import scala.concurrent._
import scalaz.effect.IO
/**
 * A fascade object to twitter's zookeeper client. We'll use this to add a path, update a path,
 * delete a path, and return the value of a path.
 * Option will be provided to update the watchers when a nodestatus changes.
 * @author ram
 *
 */
class Zoo(connectionTimeout: Option[Duration], sessionTimeout: Duration, uris: String, nodePath: String) {

  def this(uris: String, nodePath: String) =
    this(DefaultConnectionTimeout, DefaultSessionTimeout, uris, nodePath)

  /**
   * Location of the ZK server(s), loaded from the config file using ConfigFactory.
   */

  implicit val timer = new JavaTimer(true)

  private lazy val zkclient = ZkClient(uris, connectionTimeout, sessionTimeout)(timer)

  private lazy val zknode = zkclient(nodePath)
  
  exists(nodePath)

  def znode(childPath: String): ZNode = {
    val znode = zknode(childPath)
   znode
  }

  def create(node: ZNode, data: String): ValidationNel[Throwable, ZNode] = {
    (fromTryCatch {
      val parent = node.parentPath
      println("Path--->" + node.parentPath)
      println("entry")
      val znode = zkclient(parent)
      Await.result(znode.create(data.getBytes, DefaultACL, DefaultMode, child = Some("machine4")))
    } leftMap { t: Throwable =>
      new Throwable(
        """Node creation failure for :'%s'
            |
            |Because your path already exists.
            |""".format(node.parentPath).stripMargin + "\n ", t)
    }).toValidationNel.flatMap { i: ZNode => Validation.success[Throwable, ZNode](i).toValidationNel }
  }

  def setData(znode: ZNode, data: Array[Byte], version: Int): ValidationNel[Throwable, ZNode.Data] = {
    (fromTryCatch {
      Await.result(znode.setData(data, version))
    } leftMap { t: Throwable =>
      new Throwable(
        """set node data is failure for :'%s'
            |
            |
            |""".format(znode.path).stripMargin + "\n ", t)
    }).toValidationNel.flatMap { i: ZNode.Data => Validation.success[Throwable, ZNode.Data](i).toValidationNel }
  }

  def getData(path: String, znode: ZNode) {
    try {
      val data = Await.result(znode.getData())
      println("Node data - " + new String(data.bytes))
    } catch {
      case e: NullPointerException =>
        println("====" + e)
      case ke: KeeperException.NoNodeException =>
        {
          println("Node doesn't exists")
        }
    }
  }

  def getChildren(znode: ZNode) = {
    try {
      val child = Await.result(znode.getChildren())
      println("Children - " + child.children)
    } catch {
      case e: NullPointerException =>
        println("====" + e)
      case ke: KeeperException.NoNodeException =>
        {
          println("Node doesn't exists")
        }
    }

  }

  def exists(path: String) {
    try {
      val znode = zkclient(path)
      val child = Await.result(znode.exists())
      println("Path already exists")
    } catch {
      case e: NullPointerException =>
        println("====" + e)
      case ke: KeeperException =>
        {
          create(znode(path), "created")
          println("Node doesn't exists")
        }
    }
  }
}

object Zoo {

  /**
   * Timeout value as loaded from the config file using ConfigFactory.
   */

  private[Zoo] val DefaultConnectionTimeout: Option[Duration] = Some(Duration(100, TimeUnit.SECONDS))

  private[Zoo] val DefaultSessionTimeout: Duration = Duration(1200, TimeUnit.SECONDS)

  private[Zoo] val DefaultParentPath: ZooPath = "/machines"

  //ZooKeeperUtils.ensurePath(zkClient, ACL, parentPath);

  //def apply(uris: String, nodePath: String) = new Zoo(DefaultConnectionTimeout,uris, nodePath)

}

