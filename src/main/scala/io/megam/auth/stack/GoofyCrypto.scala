package io.megam.auth.stack

import scalaz._
import Scalaz._
import scalaz.Validation._
import scalaz.effect.IO
import scalaz.EitherT._
import scalaz.NonEmptyList._

import java.security.MessageDigest
import javax.crypto.spec.SecretKeySpec
import javax.crypto.Mac

import base64.Encode.{ apply => toBase64 }
import base64.Encode.{ urlSafe => toBase64UrlSafe }

import io.megam.auth.funnel._
import io.megam.auth.funnel.FunnelErrors._
import io.megam.auth.stack._
import play.api.http.Status._
/**
 * GoofyCrypto just provides methods to make a content into MD5,
 * calculate a HMACSHA1, using a RAW secret (api_key).
 */
object GoofyCrypto {

    
    def compareFor(ah: AuthBagHMAC, prefix: String): IO[scalaz.\/[  NonEmptyList[Throwable],Option[AuthBag]]] = {
      if (ah.dbhmac === ah.hmac) {
        play.api.Logger.debug(("%-20s -->[%s]").format("GOOF ", prefix + "  ✓"))
        (ah.bag.some).right[NonEmptyList[Throwable]].pure[IO]
      } else {
        play.api.Logger.debug(("%-20s -->[%s]").format("GOOF ", prefix + "  ✘"))
        (nels((CannotAuthenticateError("Authorization failure: ", prefix, UNAUTHORIZED))): NonEmptyList[Throwable]).left[Option[AuthBag]].pure[IO]
        
      }
    }

    
  /**
   * Calculate the MD5 hash for the specified content (UTF-16 encoded)
   */
  def toMD5(content: Option[String]): Option[String] = {
    val digest = MessageDigest.getInstance("MD5")
    digest.update(content.getOrElse(new String()).getBytes)
    val md5b = new String(toBase64UrlSafe(digest.digest()))
    md5b.some
  }

  /**
   * Calculate the HMAC for the specified data and the supplied secret (UTF-16 encoded)
   */
  def toHMAC(secret: String, toEncode: String): String = {
    val signingKey = new SecretKeySpec(secret.getBytes(), "RAW")
    val mac = Mac.getInstance("HmacSHA256")
    mac.init(signingKey)
    val rawHmac = mac.doFinal(toEncode.getBytes())
    dumpByt(rawHmac.some)
  }

  
  

  private def dumpByt(bytesOpt: Option[Array[Byte]]): String = {
    val b: Array[String] = (bytesOpt match {
      case Some(bytes) => bytes.map(byt => (("00" + (byt &
        0XFF).toHexString)).takeRight(2))
      case None => Array(0X00.toHexString)
    })
    b.mkString("")
  }

}
