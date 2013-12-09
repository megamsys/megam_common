/* 
** Copyright [2012] [Megam Systems]
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
/**
 * @author rajthilak
 *
 */

import org.specs2._
import scalaz._
import Scalaz._
import org.specs2.mutable._
import org.specs2.Specification
import org.megam.common._
import com.twitter.zk._
import com.twitter.util.{ Duration, Future, Promise, TimeoutException, Timer, Return, Await }
import org.apache.zookeeper.data.{ ACL, Stat }
import org.apache.zookeeper.KeeperException

class ZooSpecs extends Specification {

  def is =
    "ZooSpecs".title ^ end ^
      """
  ZooKeeper Client creation and node path
  """ ^ end ^
      "The Zoo Client Should" ^
      "Correctly do a node path check" ! ZooNode().createSucceeds ^
      "Set the data to existing node" ! ZooNode().setDataSucceeds ^
      "Get the data from existing node" ! ZooNode().getDataSucceeds ^
       "Get the children name from existing node" ! ZooNode().getChildrenSucceeds ^
      "Delete the existing node" ! ZooNode().deleteSucceeds ^
      "Watch children from existing node" ! ZooNode().watchChildrenSucceeds ^
      "Watch Data from existing node" ! ZooNode().watchDataSucceeds ^
      "Monitor data from existing node" ! ZooNode().monitorDataSucceeds ^
      end

  trait TestContext {

    println("Setting up ZooKeeper Client")

    lazy val zoo = new Zoo("localhost:2181", "testing")

    val path = "/machines/testing/redis"
    val parent = "/machines3/sample"
    val name = "sample"
    val path1 = "/machines/testing"
    //zoo.exists(path)   
    val child = "testing/redis"
    /*val t: ValidationNel[Throwable, ZNode] = zoo.create(path, "created")
    t match {
      case Success(t1) => {
        println("Value stored success" + t1)
      }
      case Failure(f) => {
        println("Failure" + f)
      }
    }*/
    /*val childpath = znode.path
    zoo.create(childpath, null)(Future(znode.path))

    //zoo.setData(znode, "".getBytes, 0)   
    val znode11 = zoo.znode("node1")
    zoo.getData(znode11.path, znode11)
*/
    //val znode1 = zoo.zknode
    //zoo.getChildren(znode1)

    /*val znode1 = zoo.znode("node1")
    val childpath1 = znode1.path
    zoo.create(childpath1, null)(Future(znode1.path))

    zoo.setData(znode1, "child started".getBytes, 0)
   
    zoo.getData(znode1.path, znode1)
*/

    //val result1 = ZNode.Exists(zk.zknode, new Stat)
    //println("======="+(result1.stat).getCtime())
    //zk.exists(path)(Future(result1.stat))    

    protected def createExecute[T](t: ValidationNel[Throwable, ZNode], expectedPath: String = path) = {
      println("Executing ZooNode")

      val res: ZNode = t match {
        case Success(zn) => zn
        case Failure(errThrown) => {
          println("*=----------------------------------------*\n")
          errThrown.head.printStackTrace
          println("*=----------------------------------------*\n")
          null
        }
      }
      println("-->" + res)
      res.path mustEqual expectedPath
    }

    protected def deleteExecute[T](t: ValidationNel[Throwable, ZNode], expectedPath: String = path) = {
      println("Deleting ZooNode")

      val res: ZNode = t match {
        case Success(zn) => zn
        case Failure(errThrown) => {
          println("*=----------------------------------------*\n")
          errThrown.head.printStackTrace
          println("*=----------------------------------------*\n")
          null
        }
      }
      println("-->" + res)
      res.path mustEqual expectedPath
    }

    protected def setDataExecute[T](t: ValidationNel[Throwable, ZNode.Data], expectedPath: String = path) = {
      println("Executing ZooNode")

      val res: ZNode = t match {
        case Success(zn) => zn
        case Failure(errThrown) => {
          println("*=----------------------------------------*\n")
          errThrown.head.printStackTrace
          println("*=----------------------------------------*\n")
          null
        }
      }
      println("-->" + res)
      res.path mustEqual expectedPath
    }

    protected def getDataExecute[T](t: ValidationNel[Throwable, String], expected: String = "started") = {
      println("Executing ZooNode")
      //val x = zoo.watchChildren(path1)
      //println("=="+x)
      zoo.monitorChildren(path1)
      val res: String = t match {
        case Success(zn) => zn
        case Failure(errThrown) => {
          println("*=----------------------------------------*\n")
          errThrown.head.printStackTrace
          println("*=----------------------------------------*\n")
          null
        }
      }
      println("-->" + res)
      res mustEqual expected
    }

    protected def getChildrenExecute[T](t: ValidationNel[Throwable, Seq[ZNode]], expected: Seq[ZNode] = Seq(zoo.znode(child))) = {
      println("Executing ZooNode")

      val res: Seq[ZNode] = t match {
        case Success(zn) => zn
        case Failure(errThrown) => {
          println("*=----------------------------------------*\n")
          errThrown.head.printStackTrace
          println("*=----------------------------------------*\n")
          null
        }
      }
      println("-->" + res)
      res mustEqual expected
    }

    protected def watchChildrenExecute[T](t: ValidationNel[Throwable, Future[ZNode.Watch[ZNode.Children]]], expected: String = path) = {
      println("Executing ZooNode")

      val res = t match {
        case Success(zn) => zn.onSuccess {
          case ZNode.Watch(Return(z), u) => { 
            z.path mustEqual expected 
            println("Node path: %s".format(z))
            u onSuccess {
              //case NodeEvent.ChildrenChanged(name) => logger.debug("Node Name: %s".format(name))
              case e => {
                println("Event: %s".format(e))
                val rr = zoo.setData(zoo.znode(child), "child started".getBytes, 0)
                println(rr)               
              }
            }
          }
          case _ => println("unexpected return value")
        }
        case Failure(errThrown) => {
          println("*=----------------------------------------*\n")
          errThrown.head.printStackTrace
          println("*=----------------------------------------*\n")
          null
        }
      }
      val path2 = "/machines/nodes/riak"
      println("-->" + res)
      path2 mustEqual expected
    }
    
    protected def watchDataExecute[T](t: ValidationNel[Throwable, Future[ZNode.Watch[ZNode.Data]]], expected: String = path) = {
      println("Executing ZooNode")

      val res = t match {
        case Success(zn) => zn.onSuccess {
          case ZNode.Watch(Return(z), u) => {           
            z.path mustEqual expected 
            println("Node path: %s".format(z))
            u onSuccess {
              //case NodeEvent.ChildrenChanged(name) => logger.debug("Node Name: %s".format(name))
              case e => {
                println("Event: %s".format(e))                
                val rr = zoo.setData(zoo.znode(child), "child started".getBytes, 0)
                println(rr)               
              }
            }
          }
          case _ => println("unexpected return value")
        }
        case Failure(errThrown) => {
          println("*=----------------------------------------*\n")
          errThrown.head.printStackTrace
          println("*=----------------------------------------*\n")
          null
        }
      }
     val path2 = "/machines/nodes/nodejs"
      println("-->" + res)
      path2 mustEqual expected
    }
    
     protected def monitorChildDeleteExecute[T](t: ValidationNel[Throwable, String], expected: String = "started") = {
      println("Executing ZooNode")
      //val x = zoo.watchChildren(path1)
      //println("=="+x)
      zoo.monitorChildDelete(path)
      val res: String = t match {
        case Success(zn) => zn
        case Failure(errThrown) => {
          println("*=----------------------------------------*\n")
          errThrown.head.printStackTrace
          println("*=----------------------------------------*\n")
          null
        }
      }
      println("-->" + res)
      res mustEqual expected
    }

  }

  case class ZooNode() extends TestContext {

    def createSucceeds = createExecute(zoo.create(child, "child created"))
    def setDataSucceeds = setDataExecute(zoo.setData(zoo.znode(child), "child started".getBytes, 0))
    def getDataSucceeds = getDataExecute(zoo.getData((zoo.znode(child)).path, zoo.znode(child)))
    def getChildrenSucceeds = getChildrenExecute(zoo.getChildren((zoo.zknode)))
    def deleteSucceeds = deleteExecute(zoo.delete(path, 0))
    def watchChildrenSucceeds = watchChildrenExecute(zoo.watch[ValidationNel[Throwable, Future[ZNode.Watch[ZNode.Children]]]](zoo.watchChildren, path1))
    def watchDataSucceeds = watchDataExecute(zoo.watch[ValidationNel[Throwable, Future[ZNode.Watch[ZNode.Data]]]](zoo.watchData, path1))
    def monitorDataSucceeds = monitorChildDeleteExecute(zoo.getData((zoo.znode(child)).path, zoo.znode(child)))
  }

}





