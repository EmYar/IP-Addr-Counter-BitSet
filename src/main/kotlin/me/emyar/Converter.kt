package me.emyar

/**
 * This function is used instead of String#split because of performance: it does not allocate an array and skips
 * from 6 to 8 (depends on last byte length) characters when looking for dots
 */
fun String.toIpInt(): UInt {
    var currentResult = 0u

    var byteStartPosition = 0
    var dotsFound = 0
    var bitShift = 24

    var i = 1 // first char is always a digit
    while (dotsFound < 3) {
        val char = this[i]
        if (char == '.') {
            currentResult = currentResult or (substring(byteStartPosition, i).toUInt() shl bitShift)
            byteStartPosition = i + 1
            dotsFound++
            bitShift -= 8
            i += 2 // skip first character after found '.'
        } else {
            i++
        }
    }

    return currentResult or substring(byteStartPosition, length).toUInt()
}