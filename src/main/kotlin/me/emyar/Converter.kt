package me.emyar

fun String.toIpInt(): UInt {
    var currentResult = 0u

    var byteStartPosition = 0
    var dotsFound = 0
    var bitShift = 24

    var i = 1
    var skipChar = true
    for (char in this) {
        if (skipChar) {
            skipChar = false
            continue
        }
        if (char == '.') {
            currentResult = currentResult or substring(byteStartPosition, i).toUInt().shl(bitShift)
            byteStartPosition = i + 1
            dotsFound++
            if (dotsFound == 3)
                return currentResult or substring(byteStartPosition, length).toUInt()
            bitShift -= 8
            i += 2 // skip first digit after '.'
            skipChar = true
        } else
            i++
    }

    throw IllegalArgumentException("Third '.' not found in IP '$this'")
}