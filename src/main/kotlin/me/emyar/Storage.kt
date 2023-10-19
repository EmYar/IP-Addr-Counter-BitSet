package me.emyar

import java.util.*
import java.util.stream.IntStream

class Storage {

    private val firstStorage = BitSet(Int.MAX_VALUE)
    private val secondStorage = BitSet(Int.MAX_VALUE)

    var uniqueIpsCount: Long = 0
        private set

    fun add(ip: Int) {
        if (ip < 0) {
            addToBitSet(firstStorage, ip - Int.MIN_VALUE)
        } else {
            addToBitSet(secondStorage, ip)
        }
    }

    operator fun plusAssign(ip: Int) = add(ip)

    /**
     * Returns a stream of indices for which this Storage contains a bit in the set state.
     * The indices are returned in order, from lowest to highest.
     */
    fun stream(): IntStream =
        IntStream.concat(
            firstStorage.stream()
                .map { it - Int.MIN_VALUE },
            secondStorage.stream()
        )

    private fun addToBitSet(bitSet: BitSet, index: Int) {
        if (!bitSet[index]) {
            bitSet.set(index)
            uniqueIpsCount++
        }
    }
}