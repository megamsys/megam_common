/*
** Copyright [2013-2016] [Megam Systems]
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
package io.megam.json
/*
import scalaz._
import scalaz.NonEmptyList._
import scalaz.Validation
import scalaz.Validation._
import Scalaz._
import net.liftweb.json._
import net.liftweb.json.scalaz.JsonScalaz._
import java.util.Date
import java.nio.charset.Charset
import io.megam.auth.funnel.FunnelErrors._

import io.megam.common.Constants._
import io.megam.auth.stack.AccountResult

/**
 * @author ram
 *
 */
class AccountResultSerialization(charset: Charset = UTF8Charset) extends SerializationBase[AccountResult] {

  protected val JSONClazKey = io.megam.common.Constants.JSON_CLAZ
  protected val IdKey = "id"
  protected val NameKey = "name"

  protected val PhoneKey = "phone"
  protected val EmailKey = "email"
  protected val APIKey = "api_key"
  protected val PasswordKey = "password"
  protected val StatesKey = "states"
  //protected val OrgKey = "org_id"
  protected val ApprovalKey = "approval"
  protected val SuspendKey = "suspend"
  protected val RegistrationIpAddressKey = "registration_ip_address"
  protected val DatesKey = "dates"

  override implicit val writer = new JSONW[AccountResult] {

    override def write(h: AccountResult): JValue = {
      JObject(
        JField(IdKey, toJSON(h.id)) ::
          JField(NameKey, toJSON(h.name)) ::
          JField(PhoneKey, toJSON(h.phone)) ::
          JField(EmailKey, toJSON(h.email)) ::
          JField(APIKey, toJSON(h.api_key)) ::
          JField(PasswordKey, toJSON(h.password)) ::
          JField(StatesKey, toJSON(h.states)) ::
          //JField(OrgKey, toJSON(h.org_id))    ::
          JField(ApprovalKey, toJSON(h.approval)) ::
          JField(SuspendKey, toJSON(h.suspend)) ::
          JField(RegistrationIpAddressKey, toJSON(h.registration_ip_address)) ::
          JField(DatesKey, toJSON(h.dates)) ::
          JField(JSONClazKey, toJSON("Megam::Account")) :: Nil)
    }
  }

  override implicit val reader = new JSONR[AccountResult] {

    override def read(json: JValue): Result[AccountResult] = {
      val idField = field[String](IdKey)(json)
      val nameField = field[Name](NameKey)(json)
      val phoneField = field[Phone](PhoneKey)(json)

      val emailField = field[String](EmailKey)(json)
      val apiKeyField = field[String](APIKey)(json)
      val passwordField = field[String](PasswordKey)(json)
      val statesField = field[States](StatesKey)(json)
      //val orgField = field[String](OrgKey)(json)

      val approvalField = field[S](ApprovalKey)(json)
      val suspendField = field[String](SuspendKey)(json)
      val registrationIpAddressField = field[String](RegistrationIpAddressKey)(json)
      val datesField = field[String](DatesKey)(json)

      (idField |@| nameField  |@| phoneField |@| emailField |@| apiKeyField |@| passwordField |@| statesField |@| approvalField |@| suspendField |@| registrationIpAddressField |@| datesField) {
        (id: String, name: Name, phone: Phone, email: String, apikey: String, password: Password, states: States, approval: Approval, suspend: Suspend, registration_ip_address: String, dates: Dates) =>
          new AccountResult(id, name,  phone, email, apikey, password, states, approval, suspend, registration_ip_address, dates)
      }
    }
  }

}
*/
