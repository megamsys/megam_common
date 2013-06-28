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
      //"Add child path" ! ZooNode().addChildSucceeds ^
      end

  trait TestContext {

    println("Setting up ZooKeeper Client")

    lazy val zoo = new Zoo("localhost:2181", "/machines4")

    val path = "nodes"
    val parent = "/"
    val name = "nodes"

    //zoo.exists(path)   

    val childPath = "nodes"
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
      println("-->"+res)      
      res.path mustEqual expectedPath    
    }
 
    protected def addChildExecute[T](t: ValidationNel[Throwable, ZNode], expectedPath: String = "/machines4") = {
      println("Executing ZooNode")

      val res: ZNode = t match {
      case Success(zn) => {
        val s1 = zoo.create(zn, null)
        println("+++++"+s1)
        zn
      }
      case Failure(errThrown) => {
        println("*=----------------------------------------*\n")
        errThrown.head.printStackTrace
        println("*=----------------------------------------*\n")
        null
      }
    }
      println("-->"+res)      
      res.path mustEqual expectedPath    
    }
  

  /* protected def setDataExecute[T](t: ValidationNel[Throwable, ZNode.Data], expectedPath: String = path) = {
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
      println("-->"+res)      
      res.path mustEqual expectedPath    
    }*/
  }

  
  case class ZooNode() extends TestContext {
    
    def createSucceeds = createExecute(zoo.create(zoo.znode(path) , "created"))
    //def addChildSucceeds = addChildExecute(zoo.znode(path))
   //def setDataSucceeds = setDataExecute(zoo.setData(znode, "child started".getBytes, 0))
  }
 
}





