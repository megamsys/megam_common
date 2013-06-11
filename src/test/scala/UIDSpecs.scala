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
/**
 * @author subash
 *
 */
class UIDSpecs {
  
  def is =
    "UIDSpecs".title ^ end ^
      """
  UID is an implementation of TwitterSnowflakeId service 
  """ ^ end ^
      "The UID Client Should" ^
      "Correctly return a Unique ID" !UID().succeeds ^
      end
      
      protected def ensureUIDOk(h: Long ) = h.code must beEqualTo(1111)
  }

  case class UID()  {
    
    def succeeds = (new UID("sub"))).get(ensureUIDOk(_))
  }

}
      
      
      
      

}