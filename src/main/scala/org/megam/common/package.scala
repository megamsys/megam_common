package org.megam

import java.nio.charset.Charset
import scalaz._
import scalaz.effect.IO
import scalaz.NonEmptyList._
import Scalaz._
import org.apache.zookeeper.ZooDefs.Ids
import org.apache.zookeeper._
import org.apache.zookeeper.CreateMode
import org.apache.zookeeper.data.ACL
import org.apache.zookeeper.ZooDefs.Ids.OPEN_ACL_UNSAFE
import scala.collection.JavaConverters._
import com.twitter.util.Stopwatch

package object common {

  type IOValidation[Fail, Success] = IO[Validation[Fail, Success]]

  type ZooPath = String

  val DefaultACL: Seq[ACL] = OPEN_ACL_UNSAFE.asScala  
  
  //val DefaultMode: CreateMode = CreateMode.PERSISTENT
  val DefaultMode: CreateMode = CreateMode.EPHEMERAL_SEQUENTIAL
  
  val ZooRootPath = "/machines"
  
  val  TwitStopwatch = com.twitter.util.Stopwatch 
  
  
  
}