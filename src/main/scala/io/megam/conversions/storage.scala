/*
** Copyright [2013-2016] [Megam Systems]
**
** https://opensource.org/licenses/MIT
**
*/
package io.megam
package conversions

/**
 * @author ram
 *
 */

import io.megam.util.StorageUnit

object storage {
  class RichWholeNumber(wrapped: Long) {
    def byte      = bytes
    def bytes     = new StorageUnit(wrapped)
    def kilobyte  = kilobytes
    def kilobytes = new StorageUnit(wrapped * 1024)
    def megabyte  = megabytes
    def megabytes = new StorageUnit(wrapped * 1024 * 1024)
    def gigabyte  = gigabytes
    def gigabytes = new StorageUnit(wrapped * 1024 * 1024 * 1024)
    def terabyte  = terabytes
    def terabytes = new StorageUnit(wrapped * 1024 * 1024 * 1024 * 1024)
    def petabyte  = petabytes
    def petabytes = new StorageUnit(wrapped * 1024 * 1024 * 1024 * 1024 * 1024)

    def thousand  = wrapped * 1000
    def million   = wrapped * 1000 * 1000
    def billion   = wrapped * 1000 * 1000 * 1000
  }

  implicit def intToStorageUnitableWholeNumber(i: Int) = new RichWholeNumber(i)
  implicit def longToStorageUnitableWholeNumber(l: Long) = new RichWholeNumber(l)
}
