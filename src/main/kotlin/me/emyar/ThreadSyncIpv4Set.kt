package me.emyar

import java.util.*
import java.util.concurrent.atomic.AtomicLong

private val firstStorageMaxIp = Int.MAX_VALUE.toUInt()
private val secondStorageMaxIp = firstStorageMaxIp * 2u
private val secondStorageIndexShift = firstStorageMaxIp.inc()

class ThreadSyncIpv4Set {
    private val firstStorage = BitSet(Int.MAX_VALUE)
    private val secondStorage = BitSet(Int.MAX_VALUE)

    @Volatile
    private var isLastIpStored = false

    private val _uniqueIpsCount = AtomicLong()
    val uniqueIpsCount: Long
        get() = _uniqueIpsCount.get()

    operator fun plusAssign(ip: UInt) = add(ip)

    fun add(ip: UInt) {
        when {
            ip <= firstStorageMaxIp -> addToBitSet(firstStorage, ip.toInt())
            ip <= secondStorageMaxIp -> addToBitSet(secondStorage, (ip - secondStorageIndexShift).toInt())
            ip == UInt.MAX_VALUE -> saveLastIp()
        }
    }

    private fun addToBitSet(bitSet: BitSet, index: Int) {
        if (!bitSet[index]) {
            var stateChanged = false
            synchronized(bitSet) {
                if (!bitSet[index]) {
                    bitSet.set(index)
                    stateChanged = true
                }
            }
            if (stateChanged)
                _uniqueIpsCount.incrementAndGet()
        }
    }

    private fun saveLastIp() {
        if (!isLastIpStored) {
            var stateChanged = false
            synchronized(this) {
                if (!isLastIpStored) {
                    isLastIpStored = true
                    stateChanged = true
                }
            }
            if (stateChanged)
                _uniqueIpsCount.incrementAndGet()
        }
    }
}