package me.emyar

import java.util.*

private val firstStorageMaxIp = Int.MAX_VALUE.toUInt()
private val secondStorageMaxIp = firstStorageMaxIp * 2u
private val secondStorageIndexShift = firstStorageMaxIp.inc()

class Ipv4Set {
    private val firstStorage = BitSet(Int.MAX_VALUE)
    private val secondStorage = BitSet(Int.MAX_VALUE)

    @Suppress("PrivatePropertyName")
    private var ip255_255_255_255_isSet = false

    private var _uniqueIpsCount = 0L
    val uniqueIpsCount get() = _uniqueIpsCount

    fun add(ip: UInt) {
        when {
            ip <= firstStorageMaxIp -> addToBitSet(firstStorage, ip.toInt())
            ip <= secondStorageMaxIp -> addToBitSet(secondStorage, (ip - secondStorageIndexShift).toInt())
            ip == UInt.MAX_VALUE && !ip255_255_255_255_isSet -> {
                ip255_255_255_255_isSet = true
                _uniqueIpsCount++
            }
        }
    }

    private fun addToBitSet(bitSet: BitSet, index: Int) {
        if (!bitSet[index]) {
            bitSet.set(index)
            _uniqueIpsCount++
        }
    }
}