package me.emyar

import java.util.*
import java.util.concurrent.atomic.AtomicBoolean
import java.util.concurrent.atomic.AtomicLong

private val firstStorageMaxIp = Int.MAX_VALUE.toUInt()
private val secondStorageMaxIp = firstStorageMaxIp * 2u
private val secondStorageIndexShift = firstStorageMaxIp.inc()

class Ipv4Set {
    private val firstStorage = BitSet(Int.MAX_VALUE)
    private val secondStorage = BitSet(Int.MAX_VALUE)

    @Suppress("PrivatePropertyName")
    private val ip255_255_255_255_isSet = AtomicBoolean()

    private val _uniqueIpsCount = AtomicLong()
    val uniqueIpsCount: Long
        get() = _uniqueIpsCount.get()

    operator fun plusAssign(ip: UInt) = add(ip)

    fun add(ip: UInt) {
        when {
            ip <= firstStorageMaxIp -> addToBitSet(firstStorage, ip.toInt())
            ip <= secondStorageMaxIp -> addToBitSet(secondStorage, (ip - secondStorageIndexShift).toInt())
            ip == UInt.MAX_VALUE && !ip255_255_255_255_isSet.get() -> {
                synchronized(this) {
                    if (!ip255_255_255_255_isSet.get()) {
                        ip255_255_255_255_isSet.set(true)
                        _uniqueIpsCount.incrementAndGet()
                    }
                }
            }
        }
    }

    private fun addToBitSet(bitSet: BitSet, index: Int) {
        if (!bitSet[index]) {
            synchronized(bitSet) {
                if (!bitSet[index]) {
                    bitSet.set(index)
                    _uniqueIpsCount.incrementAndGet()
                }
            }
        }
    }
}