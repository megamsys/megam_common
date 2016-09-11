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

    def verifyPW(funldReq:  FunneledRequest, dbHMAC: String): IO[scalaz.\/[  NonEmptyList[Throwable],Option[AuthBag]]] = {
      if (dbHMAC === funldReq.clientAPIHmac.get) {
        play.api.Logger.debug(("%-20s -->[%s]").format("GOOF PW  ✓", dbHMAC))
        //(AuthBag(freq.email, freq.maybeOrg.get, fres.password.password_hash, fres.states.authority).some).right[NonEmptyList[Throwable]].pure[IO]
        (AuthBag("", funldReq.maybeOrg.get, "","").some).right[NonEmptyList[Throwable]].pure[IO]
      } else {
        play.api.Logger.debug(("%-20s -->[%s]").format("PW Auth error", ""))
        (nels((CannotAuthenticateError("""Authorization failure for 'email:' PW-HMAC doesn't match: '%s'.""".format("").stripMargin, "", UNAUTHORIZED))): NonEmptyList[Throwable]).left[Option[AuthBag]].pure[IO]
      }
    }

    def verifyAPI(funldReq:  FunneledRequest, dbHMAC: String): IO[scalaz.\/[NonEmptyList[Throwable],Option[AuthBag]]] = {
      if (dbHMAC === funldReq.clientAPIHmac.get) {
        play.api.Logger.debug(("%-20s -->[%s]").format("GOOF API ✓", dbHMAC))
        //(AuthBag(freq.email, freq.maybeOrg.get, fres.api_key, fres.states.authority).some).right[NonEmptyList[Throwable]].pure[IO]
        (AuthBag("", funldReq.maybeOrg.get, "","").some).right[NonEmptyList[Throwable]].pure[IO]
      } else {
        play.api.Logger.debug(("%-20s -->[%s]").format("API Auth error", ""))
        (nels((CannotAuthenticateError("""Authorization failure for 'email:' API-HMAC doesn't match: '%s'.""".format("").stripMargin, "", UNAUTHORIZED))): NonEmptyList[Throwable]).left[Option[AuthBag]].pure[IO]
      }
    }

  /**
   * Calculate the MD5 hash for the specified content (UTF-16 encoded)
   */
  def calculateMD5(content: Option[String]): Option[String] = {
    val digest = MessageDigest.getInstance("MD5")
    digest.update(content.getOrElse(new String()).getBytes)
    val md5b = new String(toBase64UrlSafe(digest.digest()))
    md5b.some
  }

  /**
   * Calculate the HMAC for the specified data and the supplied secret (UTF-16 encoded)
   */
  def calculateHMAC(secret: String, toEncode: String): String = {
    val HMACSHA1 = "HmacSHA1"
    val signingKey = new SecretKeySpec(secret.getBytes(), "RAW")
    val mac = Mac.getInstance(HMACSHA1)
    mac.init(signingKey)
    val rawHmac = mac.doFinal(toEncode.getBytes())
    val hmacAsByt = dumpByt(rawHmac.some)
    hmacAsByt
  }

  def dumpByt(bytesOpt: Option[Array[Byte]]): String = {
    val b: Array[String] = (bytesOpt match {
      case Some(bytes) => bytes.map(byt => (("00" + (byt &
        0XFF).toHexString)).takeRight(2))
      case None => Array(0X00.toHexString)
    })
    b.mkString("")
  }

}
