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
import org.slf4j.LoggerFactory
import org.apache.zookeeper.WatchedEvent
/**
 * A fascade object to twitter's zookeeper client. We'll use this to add a path, update a path,
 * delete a path, and return the value of a path.
 * Option will be provided to update the watchers when a nodestatus changes.
 * @author ram
 *
 */
class Zoo(connectionTimeout: Option[Duration], sessionTimeout: Duration, uris: String, parentPath: String) {

  def this(uris: String, parentPath: String) =
    this(DefaultConnectionTimeout, DefaultSessionTimeout, uris, parentPath)


  /**
   * Location of the ZK server(s), loaded from the config file using ConfigFactory.
   */

  implicit val timer = new JavaTimer(true)

  /**
   * create zkclient for some uris
   */
  private lazy val zkclient = ZkClient(uris, connectionTimeout, sessionTimeout)(timer)

  /**
   * to verify our root path is already exists in zookeeper
   * if path doesn't exists this function create the root path
   *
   */
  exists(ZooRootPath)

  /**
   * create the znode for root path
   *
   */
  private lazy val zrnode: ZNode = zkclient(ZooRootPath)

  /**
   * This will get the child name
   * and return znode for that child path
   *
   */
  def znode(childPath: String): ZNode = {
    val znode = zrnode(childPath)   
    znode
  }

  /**
   * create child path and check path already exists or not
   *
   */
  val zknode: ZNode = znode(parentPath)
  exists(zknode.path)

  /**
   * These function create a new child already existing parent path
   * child already exists then this will return stack trace
   */
  def create(child: String, data: String): ValidationNel[Throwable, ZNode] = {
    (fromTryCatch {
      Await.result(zknode.create(data.getBytes, DefaultACL, DefaultMode, child = Some(child)))
    } leftMap { t: Throwable =>
      new Throwable(
        """Node creation failure for :'%s'
            |
            |Path creation failed for some reasons. Please see following full stack trace.
            |""".format("/" + parentPath + "/" + child).stripMargin + "\n ", t)
    }).toValidationNel.flatMap { i: ZNode => Validation.success[Throwable, ZNode](i).toValidationNel }
  }

  /**
   * These function set the new data to particular node.
   * But the node already have any data this will return error
   *
   */
  def setData(znode: ZNode, data: Array[Byte], version: Int): ValidationNel[Throwable, ZNode.Data] = {
    (fromTryCatch {
      Await.result(znode.setData(data, version))
    } leftMap { t: Throwable =>
      new Throwable(
        """set node data is failure for :'%s'
            |
            |Your path already have some data, so we couldn't set the data.
            |""".format(znode.path).stripMargin + "\n ", t)
    }).toValidationNel.flatMap { i: ZNode.Data => Validation.success[Throwable, ZNode.Data](i).toValidationNel }
  }

  /**
   * To get data from node
   *
   */
  def getData(path: String, znode: ZNode): ValidationNel[Throwable, String] = {
    (fromTryCatch {

      Await.result(znode.getData())
    } leftMap { t: Throwable =>
      new Throwable(
        """get node data is failure for :'%s'
            |
            |
            |""".format(znode.path).stripMargin + "\n ", t)
    }).toValidationNel.flatMap { i: ZNode.Data => Validation.success[Throwable, String](new String(i.bytes)).toValidationNel }
  }

  /**
   * To get the childrens from parent path
   *
   */
  def getChildren(znode1: ZNode): ValidationNel[Throwable, Seq[ZNode]] = {
    (fromTryCatch {
      Await.result(znode1.getChildren())
    } leftMap { t: Throwable =>
      new Throwable(
        """get children from node process failure for :'%s'
            |
            |
            |""".format(znode1.path).stripMargin + "\n ", t)
    }).toValidationNel.flatMap { i: ZNode.Children => Validation.success[Throwable, Seq[ZNode]](i.children).toValidationNel }
  }

  /**
   * These function to verify the path already exists in zookeeper,
   * the path already exists this will return msg,
   * otherwise create the path
   */
  def exists(path: String) {
    try {
      val znode = zkclient(path)
      val child = Await.result(znode.exists())         
    } catch {
      case ke: KeeperException =>
        {
          val zknode1 = zkclient(path)
          zknode1.create("started".getBytes, DefaultACL, DefaultMode)          
        }
    }
  }

  /**
   * Delete the node from parent path
   *
   */
  def delete(node: String, version: Int): ValidationNel[Throwable, ZNode] = {
    (fromTryCatch {
      val znode = zkclient(node)
      Await.result(znode.delete())
    } leftMap { t: Throwable =>
      new Throwable(
        """Node Deletion failure for :'%s'
            |
            |Path deletion failed for some reasons. Please see following full stack trace.
            |""".format(node).stripMargin + "\n ", t)
    }).toValidationNel.flatMap { i: ZNode => Validation.success[Throwable, ZNode](i).toValidationNel }

  }

  def watchChildren[T](path: String): ValidationNel[Throwable, Future[ZNode.Watch[ZNode.Children]]] = {
    (fromTryCatch {
      val znode = zkclient(path)
      Await.ready(znode.getChildren.watch())
    } leftMap { t: Throwable =>
      new Throwable(
        """Watch children failure for : '%s'
          |
          |""".format(path).stripMargin + "\n", t)
    }).toValidationNel.flatMap { i: Future[ZNode.Watch[ZNode.Children]] => Validation.success[Throwable, Future[ZNode.Watch[ZNode.Children]]](i).toValidationNel

    }

  }

  def watchData[T](path: String): ValidationNel[Throwable, Future[ZNode.Watch[ZNode.Data]]] = {
    (fromTryCatch {
      val znode = zkclient(path)
      Await.ready(znode.getData.watch())
    } leftMap { t: Throwable =>
      new Throwable(
        """Watch Data failure for : '%s'
          |
          |""".format(path).stripMargin + "\n", t)
    }).toValidationNel.flatMap { i: Future[ZNode.Watch[ZNode.Data]] => Validation.success[Throwable, Future[ZNode.Watch[ZNode.Data]]](i).toValidationNel

    }
  }

  def watch[T](f: String => T, path: String) = f(path)

  def monitorChildDelete(path: String) = {
    val znode = zkclient(path)
    def monit(n: Int) {
      val results = 0 until n map { _ => ZNode.Exists(znode, new Stat) }
      val update = znode.exists.monitor()
      results foreach { result =>
        update syncWait () get ()
      }
    }
    monit(3)
  }

  def monitorChildren(path: String) = {
    val znode = zkclient(path)
    val t = getChildren((znode))
    val res: Seq[String] = t match {
      case Success(zn) => {
        val str = zn.map(a => a.name)
        str
      }
      case Failure(err) => null
    }
    val results = List(res, Seq("rr")) map { ZNode.Children(znode, new Stat, _) }
    val update = znode.getChildren.monitor()
    results foreach { result =>
      update syncWait () get ()
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

