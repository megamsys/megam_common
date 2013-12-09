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
package org.megam.common.s3

import scalaz._
import Scalaz._
import scalaz.NonEmptyList._
import java.io.{StringWriter, PrintWriter}


/**
 * @author rajthilak
 *
 */
object S3Errors {
  val tailMsg = "Refer the stacktrace for more information. If this error persits, ask for help on the forums."

  case class S3ConnectionError(ak: String, sk: String)
    extends Exception  

  case class DownloadError(vl: String)
    extends Exception  
    
    case class ListingError(bucketName: String, vl: String)
    extends Exception
    
  case class S3Error(errNel: NonEmptyList[Throwable]) extends Exception({
    errNel.map { err: Throwable =>
      err.fold(
        //connection exception           
        c => """Failed to connect to S3 bucket `access key:' '%s' `secret key:' '%s'
            |
            |Please verify your s3 credentials (accessKey, and secretKey).
            |%s""".format(c.accessKey, c.secretKey, tailMsg).stripMargin,  
        f => """Failed to download from S3 `vault location:' '%s'  
            |
            |Please verify your S3 source (accessKey, secretKey, and vault location), bucketname and the 
            |key name. 
            |%s.""".format(f.vl, tailMsg).stripMargin,  
        l => """Failed to objects listing from S3 `vault location:' '%s' from `bucket name:' '%s' 
            |
            |Please verify your S3 source (accessKey, secretKey, and vault location), bucketname and the 
            |key name. 
            |%s.""".format(l.vl, l.bucketName, tailMsg).stripMargin,    
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