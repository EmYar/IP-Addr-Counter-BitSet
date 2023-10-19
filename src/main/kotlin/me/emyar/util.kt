package me.emyar

fun printMeasuredTime(message: String, timeMs: Long) {
    println("$message: ${timeMs / 3600_000}h ${timeMs / 60_000 % 60}m ${timeMs / 1000 % 60}s ${timeMs % 1000}ms")
}