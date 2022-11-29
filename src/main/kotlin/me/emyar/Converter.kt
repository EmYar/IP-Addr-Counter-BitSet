package me.emyar

import java.text.CharacterIterator
import java.text.StringCharacterIterator

fun String.toIpInt(): UInt {
    var result = 0u
    var bitShift = 0
    val iterator = StringCharacterIterator(this, this.length)
    var digitNumber = 0
    var currentNumber = 0

    var ch = iterator.last();
    while (ch != CharacterIterator.DONE) {
        if (ch != '.') {
            currentNumber += ch.digitToInt() multiplyBy10inPow digitNumber
            digitNumber++
        } else {
            result = result or (currentNumber.toUInt() shl bitShift)
            bitShift += 8
            currentNumber = 0
            digitNumber = 0
        }
        ch = iterator.previous();
    }

    return result or (currentNumber.toUInt() shl bitShift)
}

private infix fun Int.multiplyBy10inPow(pow: Int) =
    when (pow) {
        0 -> this
        1 -> this * 10
        2 -> this * 100
        else -> throw IllegalArgumentException()
    }