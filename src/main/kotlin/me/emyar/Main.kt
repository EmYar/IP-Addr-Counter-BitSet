package me.emyar

import me.emyar.runners.MultithreadingRun
import me.emyar.runners.SingleThreadRun
import java.io.File
import java.math.BigDecimal
import java.math.RoundingMode
import java.nio.file.Paths
import kotlin.time.Duration
import kotlin.time.measureTime

fun main(args: Array<String>) {
    val multiThread =
        args.getOrNull(0)
            ?.toBoolean() != false

    val inputFile: File =
        args.getOrNull(1)
            ?.let(Paths::get)
            ?.toFile()
            ?: Paths.get(System.getProperty("user.home"), "IP-Addr-Counter", "ip_addresses").toFile()

    println("Multi thread: $multiThread")
    println("File path: '$inputFile'")

    val runner = when (multiThread) {
        true -> MultithreadingRun(inputFile)
        false -> SingleThreadRun(inputFile)
    }

    val uniqueIpsCount: Long
    val time = measureTime { uniqueIpsCount = runner.run() }

    println("Unique IPs count: $uniqueIpsCount")
    println("Complete in $time")
    println("Processing speed: ${getProcessingSpeedMb(inputFile, time)} MB/s")
}

private fun getProcessingSpeedMb(file: File, time: Duration): BigDecimal {
    val sizeDivider = BigDecimal.valueOf(1024)
    val sizeMb = BigDecimal.valueOf(file.length())
        .divide(sizeDivider)
        .divide(sizeDivider)
    return sizeMb.divide(BigDecimal.valueOf(time.inWholeSeconds), 2, RoundingMode.HALF_EVEN)
}