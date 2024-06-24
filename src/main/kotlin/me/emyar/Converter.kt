package me.emyar

/**
 * This function is used instead of String#split because of performance: it does not allocate an array and skips
 * from 6 to 8 (depends on last byte length) characters when looking for dots
 */
fun String.toIpInt(): Int {
    var buffer = 0
    var currentBitShift = 24

    var numberStartPosition = 0
    var dotPosition = 1 // skip first char

    do {
        while (this[dotPosition] != '.') {
            dotPosition++
        }
        val number = this.getInt(numberStartPosition, dotPosition)
        buffer = buffer or (number shl currentBitShift)
        currentBitShift -= 8
        numberStartPosition = dotPosition + 1
        dotPosition += 2 // skip first char after dot
    } while (currentBitShift > 0)

    val lastNumber = this.getInt(numberStartPosition)
    return buffer or lastNumber
}

private fun String.getInt(startIndex: Int, endIndex: Int = this.length): Int =
    when (endIndex - startIndex) {
        1 -> this[startIndex].digitToInt()
        2 -> this[startIndex].digitToInt() * 10 + this[startIndex + 1].digitToInt()
        3 -> this[startIndex].digitToInt() * 100 + this[startIndex + 1].digitToInt() * 10 + this[startIndex + 2].digitToInt()
        else -> throw IllegalStateException()
    }

fun Int.toStringIp(): String =
    "${(this shr 24) and 255}.${(this shr 16) and 255}.${(this shr 8) and 255}.${this and 255}"
