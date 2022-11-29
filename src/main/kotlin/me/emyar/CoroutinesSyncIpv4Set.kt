package me.emyar

import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import java.util.*
import java.util.concurrent.atomic.AtomicLong

private val firstStorageMaxIp = Int.MAX_VALUE.toUInt()
private val secondStorageMaxIp = firstStorageMaxIp * 2u
private val secondStorageIndexShift = firstStorageMaxIp.inc()

class CoroutinesSyncIpv4Set {
    private val firstStorage = BitSet(Int.MAX_VALUE)
    private val secondStorage = BitSet(Int.MAX_VALUE)

    @Volatile
    private var isLastIpStored = false

    private val firstStorageMutex = Mutex()
    private val secondStorageMutex = Mutex()
    private val lastIpStoredMutex = Mutex()

    private val _uniqueIpsCount = AtomicLong()
    val uniqueIpsCount: Long
        get() = _uniqueIpsCount.get()

    suspend operator fun plusAssign(ip: UInt) = add(ip)

    suspend fun add(ip: UInt) {
        when {
            ip <= firstStorageMaxIp -> addToBitSet(firstStorage, ip.toInt(), firstStorageMutex)

            ip <= secondStorageMaxIp -> addToBitSet(
                secondStorage,
                (ip - secondStorageIndexShift).toInt(),
                secondStorageMutex
            )

            ip == UInt.MAX_VALUE -> saveLastIp()
        }
    }

    private suspend fun addToBitSet(bitSet: BitSet, index: Int, mutex: Mutex) {
        if (!bitSet[index]) {
            var stateChanged = false
            mutex.withLock {
                if (!bitSet[index]) {
                    bitSet.set(index)
                    stateChanged = true
                }
            }
            if (stateChanged)
                _uniqueIpsCount.incrementAndGet()
        }
    }

    private suspend fun saveLastIp() {
        if (!isLastIpStored) {
            var stateChanged = false
            lastIpStoredMutex.withLock {
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
