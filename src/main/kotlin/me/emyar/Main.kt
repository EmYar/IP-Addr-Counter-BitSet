package me.emyar

import kotlinx.coroutines.asCoroutineDispatcher
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import java.util.concurrent.Executors.newFixedThreadPool

val defaultInputFilePath = "${System.getProperty("user.home")}/IP-Addr-Counter/ip_addresses"

const val channelsBuffer = 10

@ExperimentalUnsignedTypes
fun main(args: Array<String>) = runBlocking {
    val inputFilePath = args.getOrElse(0) { defaultInputFilePath }

    val ipStringChannel = Channel<Array<String?>>(channelsBuffer)
    val ipIntChannel = Channel<UIntArray>(channelsBuffer)

    val storage = Ipv4Storage()

    newFixedThreadPool(6).asCoroutineDispatcher().use { coroutineDispatcher ->
        launch(coroutineDispatcher) {
            readFile(inputFilePath, ipStringChannel)
        }
            .invokeOnCompletion { ipStringChannel.close() }

        launch {
            Array(3) { launch(coroutineDispatcher) { convert(ipStringChannel, ipIntChannel) } }
                .forEach { it.join() }
            ipIntChannel.close()
        }

        Array(2) { launch(coroutineDispatcher) { storage.storeIp(ipIntChannel) } }
            .forEach { it.join() }

        println(storage.getStoredUniqueIpsCount())
    }
}