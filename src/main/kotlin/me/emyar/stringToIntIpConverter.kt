package me.emyar

@ExperimentalUnsignedTypes
fun Array<String?>.toIntIpsArray(): UIntArray {
    val result = UIntArray(size)
    var i = 0
    do {
        result[i] = this[i]?.storeIpAsInt()
            ?: return result.copyOfRange(0, i)
    } while (++i < size)

    return result
}

fun String.storeIpAsInt(): UInt {
    val ipBytesStrings = splitIp()
    return ipBytesStrings[0].toUInt().shl(24)
        .or(ipBytesStrings[1].toUInt().shl(16))
        .or(ipBytesStrings[2].toUInt().shl(8))
        .or(ipBytesStrings[3].toUInt())
}

/**
 * This implementation is about 25% faster than 'kotlin.text.split(Char, Boolean, Int)' for IPv4 strings
 */
private fun String.splitIp(): Array<String> {
    val result = Array(4) { "" }
    var resultArrayIndex = 0
    var byteStartPosition = 0

    var i = 1 // every byte in IPv4 is not empty
    do {
        if (get(i) == '.') {
            result[resultArrayIndex++] = substring(byteStartPosition, i)
            byteStartPosition = i + 1
            if (resultArrayIndex == 3)
                break // there is no need to check string after third '.'
            i += 2 // skip first digit after '.'
        } else
            i++
    } while (true) // the cycle will be completed after finding the 3rd '.'
    result[resultArrayIndex] = substring(byteStartPosition, length)

    return result
}