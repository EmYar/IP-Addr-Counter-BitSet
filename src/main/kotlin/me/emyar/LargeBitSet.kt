package me.emyar

import java.util.*
import kotlin.math.ceil

class LargeBitSet(val size: ULong) {

    private val storages: Array<StorageUnit> = createStorages()

    private fun createStorages(): Array<StorageUnit> {
        val storagesCount = ceil(size.toDouble() / Int.MAX_VALUE).toInt()
        val result = Array<StorageUnit?>(storagesCount) { null }
        val elementsPerNonLastStorage = (size / storagesCount.toULong()).toInt()
        var locatedElementsCount: ULong = 0u
        var unallocatedElementsCount = size

        for (i in 0 until storagesCount - 1) {
            locatedElementsCount += elementsPerNonLastStorage.toULong()
            result[i] = StorageUnit(
                BitSet(elementsPerNonLastStorage),
                locatedElementsCount - 1u,
                elementsPerNonLastStorage.toULong() * i.toULong()
            )
            unallocatedElementsCount -= elementsPerNonLastStorage.toULong()
        }
        result[result.lastIndex] =
            StorageUnit(
                BitSet(unallocatedElementsCount.toInt()),
                size - 1u,
                elementsPerNonLastStorage.toULong() * result.lastIndex.toULong()
            )

        @Suppress("UNCHECKED_CAST")
        return result as Array<StorageUnit>
    }

    fun get(bitIndex: Int) =
        get(bitIndex.toULong())

    fun get(bitIndex: UInt) =
        get(bitIndex.toULong())

    fun get(bitIndex: ULong): Boolean {
        if (bitIndex >= size)
            throw IndexOutOfBoundsException("bitIndex >= size: $size")

        for ((storage, lastIndex, indexShift) in storages)
            if (bitIndex <= lastIndex)
                return storage.get((bitIndex - indexShift).toInt())

        throw IllegalStateException("Index $bitIndex is not found in storages. Check 'createStorages' method")
    }

    fun set(bitIndex: Int) =
        set(bitIndex.toULong())

    fun set(bitIndex: UInt) =
        set(bitIndex.toULong())

    fun set(bitIndex: ULong) {
        if (bitIndex >= size)
            throw IndexOutOfBoundsException("bitIndex >= size: $size")

        for ((storage, lastIndex, indexShift) in storages)
            if (bitIndex <= lastIndex) {
                storage.set((bitIndex - indexShift).toInt())
                break
            }
    }
}

private data class StorageUnit(
    val storage: BitSet,
    val lastIndex: ULong,
    val indexShift: ULong
)