/*
** Copyright [2013-2016] [Megam Systems]
**
** https://opensource.org/licenses/MIT
**
*/
package io.megam.common

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
