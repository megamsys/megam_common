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
package org.megam.common.riak

import scalaz._
import Scalaz._
import scalaz.NonEmptyList._
import java.io.{StringWriter, PrintWriter}
import org.megam.common._


/**
 * @author ram
 *
 */
object GSErrors {
  val tailMsg = "Refer the stacktrace for more information. If this error persits, ask for help on the forums."

  case class GSConnectionError(uri: String)
    extends Exception

  case class BucketCreateError(uri: String, bucket: String)
    extends Exception

  case class BucketFetchError(uri: String, bucket: String, key: String)
    extends Exception

  case class BucketStoreError(uri: String, bucket: String, storeVal: GunnySack)
    extends Exception

  case class RiakError(errNel: NonEmptyList[Throwable]) extends Exception({
    errNel.map { err: Throwable =>
      err.fold(
        //connection exception           
        c => """Failed to connect to datasource Riak `uri:' '%s'
            |
            |Please verify your datasource (Riak host name ,port and the url).Ensure that Riak is running.
            |%s""".format(c.uri, tailMsg).stripMargin,
        //bucket exception
        b => """Failed to created bucket in Riak `uri:' '%s' `bucket:' '%s'
            |
            |Please verify your datasource (Riak host name ,port and the url) and the bucketname.
            |%s.""".format(b.uri, b.bucket, tailMsg).stripMargin,

        f => """Failed to fetch from Riak `uri:' '%s' from `bucket:' '%s' `key:' '%s' 
            |
            |Please verify your datasource (Riak host name ,port and the url), bucketname and the 
            |key name. Execute riak curl commands to see if the record exists. 
            |eg : curl http://localhost:8098/riak/accounts/content1 
            |          where `bucket:' 'accounts' `key:' 'content1' 
            |%s.""".format(f.uri, f.bucket, f.key, tailMsg).stripMargin,

        s => """Failed to store in Riak `uri:' '%s' `bucket:' '%s' `key:' '%s'
            |
            |Please verify your datasource (Riak host name ,port and the url), bucketname and the 
            |key name. Execute riak curl commands to see if the record exists. 
            |eg : curl -v -XPUT -d '{"id":"1","email":"sandy@megamsandbox.com", "api_key":"xxxxx","authority":"user"}' -H "Content-Type: application/json" http://localhost:8098/riak/accounts/content1         
            |          where `bucket:' 'accounts' `key:' 'content1'             
            |%s.""".format(s.uri, s.bucket, s.storeVal, tailMsg).stripMargin,
        t => """Ooops ! zzzzz.. Quiet. I don't know what happended.
            |                   
            |To help you debug, please read the message and the stacktrace below. 
            |=======================> Message <.!.> <=============================
            |                                 ( ^ )
            %s
            |
            |=======================> Stack trace <===============================
            |%s
            |=======================> Stack trace <===============================
   |%s.""".format(t.getLocalizedMessage, { val u = new StringWriter; t.printStackTrace(new PrintWriter(u)); u.toString }, tailMsg).stripMargin);
    }.list.mkString("\n")
  })

}