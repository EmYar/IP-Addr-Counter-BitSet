package me.emyar

import kotlinx.coroutines.asCoroutineDispatcher
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import java.nio.file.Path
import java.nio.file.Paths
import java.util.concurrent.Executors
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

    val runFunction = if (multiThread) ::runMultiThread else ::runSingleThread

    val uniqueIpsCount: Long
    val timeMs = measureTimeMillis {
        uniqueIpsCount = runFunction(inputFilePath)
    }

    println("Unique IPs count: $uniqueIpsCount")
    println("Complete in: ${timeMs / 3600_000}h ${timeMs / 60_000}m ${timeMs / 1000 % 60}s")
}

@ExperimentalUnsignedTypes
private fun runMultiThread(inputFilePath: Path): Long = runBlocking {
    val ipsContainerSize = 4096
    val channelsBufferSize = 10
    val convertersCount = 3

    val ipStringChannel = Channel<Array<String?>>(channelsBufferSize)
    val ipIntChannel = Channel<UIntArray>(channelsBufferSize)

    val storage = Ipv4Set()

    launch {
        Executors.newSingleThreadExecutor().asCoroutineDispatcher().use {
            launch(it) {
                Thread.currentThread().name = "File reader"
                inputFilePath.toFile().bufferedReader().use { reader ->
                    var array = Array<String?>(ipsContainerSize) { null }
                    var i = 0
                    reader.lineSequence()
                        .forEach { string ->
                            array[i++] = string
                            if (i == array.size) {
                                ipStringChannel.send(array)
                                array = Array(ipsContainerSize) { null }
                                i = 0
                            }
                        }
                    ipStringChannel.close()
                }
            }.join()
        }
    }

    launch {
        Executors.newFixedThreadPool(convertersCount).asCoroutineDispatcher().use {
            val jobs = Array(convertersCount) { i ->
                launch(it) {
                    Thread.currentThread().name = "Ip converter $i"
                    for (arrayStringIps in ipStringChannel)
                        ipIntChannel.send(arrayStringIps.mapNotNullToArray(String::toIpInt))
                }
            }
            jobs.forEach { it.join() }
            ipIntChannel.close()
        }
    }

    launch {
        for (ipArray in ipIntChannel)
            for (ipInt in ipArray)
                storage += ipInt
    }.join()

    storage.uniqueIpsCount
}

private fun runSingleThread(inputFilePath: Path): Long {
    val storage = Ipv4Set()

    inputFilePath.toFile().bufferedReader().use { reader ->
        reader.forEachLine { storage += it.toIpInt() }
    }

    return storage.uniqueIpsCount
}