package me.emyar

@ExperimentalUnsignedTypes
inline fun List<String>.mapToArray(mapper: (String) -> UInt): UIntArray =
    UIntArray(this.size) { mapper(this[it]) }