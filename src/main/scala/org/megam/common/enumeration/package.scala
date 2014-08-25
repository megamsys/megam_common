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


/**
 * @author ram
 *
 */
package object enumeration extends EnumerationImplicits {


  /**
   * shortcut to create an EnumReader
   * @param reader the method to convert a string to your enum
   * @tparam T the enum type to convert
   * @return an EnumReader that knows how to read a string into your enum
   */
  def enumReader[T <: Enumeration](reader: String => Option[T]): EnumReader[T] = new EnumReader[T] {
    override def read(s: String): Option[T] = reader(s)
  }

  /**
   * creates an EnumReader that converts a string into an enum if the lowercase
   * version of that string matches the lowercase of the enum
   * @param values the enumeration values that are candidates to convert
   * @tparam T the enumeration type
   * @return an EnumReader that has the aforementioned properties
   */
  def lowerEnumReader[T <: Enumeration](values: T*): EnumReader[T] = enumReader { s: String =>
    values.find { t: T =>
      t.stringVal.toLowerCase === s.toLowerCase
    }
  }

  /**
   * creates an EnumReader that converts a string into an enum if the uppercase
   * version of that string matches the uppercase of the enum
   * @param values the enumeration values that are candidates to convert
   * @tparam T the enumeration type
   * @return an EnumReader that has the aforementioned properties
   */
  def upperEnumReader[T <: Enumeration](values: T*): EnumReader[T] = enumReader { s: String =>
    values.find { t: T =>
      t.stringVal.toUpperCase === s.toUpperCase
    }
  }
}
