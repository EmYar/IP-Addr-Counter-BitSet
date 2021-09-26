package me.emyar

import kotlinx.coroutines.channels.ReceiveChannel
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import java.util.*
import java.util.concurrent.atomic.AtomicLong
import kotlin.coroutines.CoroutineContext
import kotlin.coroutines.EmptyCoroutineContext

private const val storageSize = 1_431_655_765

class Ipv4Storage {

    private val storageSizeUInt = storageSize.toUInt()
    private val firstStorageMaxIp = storageSizeUInt - 1u
    private val secondStorageMaxIp = firstStorageMaxIp * 2u + 1u
    private val secondStorageShift = storageSizeUInt
    private val thirdStorageShift = storageSizeUInt * 2u

    private val firstStorage = BitSet(storageSize)
    private val secondStorage = BitSet(storageSize)
    private val thirdStorage = BitSet(storageSize + 1)

    @ExperimentalUnsignedTypes
    suspend fun storeIp(receiveChannel: ReceiveChannel<UIntArray>) {
        for (arrayIntIps in receiveChannel)
            for (ipInt in arrayIntIps)
                when {
                    ipInt <= firstStorageMaxIp -> firstStorage.set(ipInt.toInt())
                    ipInt <= secondStorageMaxIp -> secondStorage.set((ipInt - secondStorageShift).toInt())
                    else -> thirdStorage.set((ipInt - thirdStorageShift).toInt())
                }
    }

    fun getStoredUniqueIpsCount(coroutineDispatcher: CoroutineContext = EmptyCoroutineContext): Long = runBlocking {
        val result = AtomicLong(0)

        arrayOf(firstStorage, secondStorage, thirdStorage)
            .map {
                launch(coroutineDispatcher) {
                    result.getAndAdd(it.getSetBitsCount())
                }
            }
            .forEach { it.join() }

        result.get()
    }

    private fun BitSet.getSetBitsCount(): Long {
        var sum = 0L
        var i = 0
        do {
//        i = nextSetBit(i + 1)
//        if (i == 0)
//            break
//        sum++
            if (get(i))
                sum++
        } while (++i < size())
        return sum
    }
}