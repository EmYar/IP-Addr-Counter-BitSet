package me.emyar

import java.nio.file.Path
import java.nio.file.Paths
import kotlin.system.measureTimeMillis

@ExperimentalUnsignedTypes
fun main(args: Array<String>) {
    val inputFilePath =
        args.getOrNull(0)
            ?.let(Paths::get)
            ?: Paths.get(System.getProperty("user.home"), "IP-Addr-Counter", "ip_addresses")

    val multiThread =
        args.getOrNull(1)
            ?.toBoolean()
            ?: true

    println("File path: '$inputFilePath'")
    println("Multi thread: $multiThread")

    val uniqueIpsCount: Long
    val timeMs = measureTimeMillis {
        uniqueIpsCount =
            if (multiThread)
                MultithreadingRun(inputFilePath)
                    .run()
            else
                runSingleThread(inputFilePath)
    }

    println("Unique IPs count: $uniqueIpsCount")
    println("Complete in: ${timeMs / 3600_000}h ${timeMs / 60_000}m ${timeMs / 1000 % 60}s")
}

private fun runSingleThread(inputFilePath: Path): Long {
    val storage = ThreadSyncIpv4Set()

    inputFilePath.toFile().bufferedReader().use { reader ->
        reader.forEachLine { storage += it.toIpInt() }
    }

    return storage.uniqueIpsCount
}