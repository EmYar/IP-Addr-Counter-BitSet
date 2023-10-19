package me.emyar

import me.emyar.runners.MultithreadingRun
import me.emyar.runners.SingleThreadRun
import java.nio.file.Paths
import kotlin.system.measureTimeMillis

fun main(args: Array<String>) {
    val multiThread =
        args.getOrNull(0)
            ?.toBoolean()
            ?: true

    val inputFilePath =
        args.getOrNull(1)
            ?.let(Paths::get)
            ?: Paths.get(System.getProperty("user.home"), "IP-Addr-Counter", "ip_addresses")

    println("Multi thread: $multiThread")
    println("File path: '$inputFilePath'")

    val runner = when (multiThread) {
        true -> MultithreadingRun(inputFilePath)
        false -> SingleThreadRun(inputFilePath)
    }

    val uniqueIpsCount: Long
    val timeMs = measureTimeMillis { uniqueIpsCount = runner.run() }

    println("Unique IPs count: $uniqueIpsCount")
    printMeasuredTime("Complete in", timeMs)
}