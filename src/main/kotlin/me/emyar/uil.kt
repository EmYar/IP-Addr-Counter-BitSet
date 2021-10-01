package me.emyar

@ExperimentalUnsignedTypes
inline fun Array<String?>.mapNotNullToArray(mapper: (String) -> UInt): UIntArray {
    val result = UIntArray(this.size)

    var i = 0
    @Suppress("UseWithIndex")
    for (string in this) {
        if (string == null)
            return result.copyOfRange(0, i)
        result[i++] = mapper(string)
    }

    return result
}