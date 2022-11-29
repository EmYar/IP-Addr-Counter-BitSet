@file:OptIn(ExperimentalUnsignedTypes::class, DelicateCoroutinesApi::class)

package me.emyar

import kotlinx.coroutines.*
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.channels.ReceiveChannel
import kotlinx.coroutines.channels.produce
import java.io.BufferedReader
import java.nio.file.Path

class MultithreadingRun(
    private val inputFilePath: Path,
    private val convertersNumber: Int = 3,
    private val storageWorkersNumber: Int = 2,
    private val chunkSize: Int = 4096,
    bufferSize: Int = 64,
) {

    private val ipIntChannel = Channel<UIntArray>(bufferSize)
    private val storage = Ipv4Set()

    fun run(): Long = runBlocking {
        newSingleThreadContext("FileReader").use { fileReaderContext ->
            newFixedThreadPoolContext(convertersNumber, "IpsConverter").use { convertersContext ->
                newFixedThreadPoolContext(storageWorkersNumber, "StorageWorker").use { storageWorkersContext ->
                    inputFilePath.toFile().bufferedReader().use { reader ->
                        val ipStringChannel = launchFileReader(fileReaderContext, reader)
                        launchConverters(convertersContext, ipStringChannel)
                        launchStorageWorkers(storageWorkersContext)
                        storage.uniqueIpsCount
                    }
                }
            }
        }
    }

    @OptIn(ExperimentalCoroutinesApi::class)
    private fun CoroutineScope.launchFileReader(
        fileReaderContext: ExecutorCoroutineDispatcher,
        reader: BufferedReader
    ) = produce(fileReaderContext, chunkSize) {
        reader.lineSequence()
            .chunked(chunkSize)
            .forEach { send(it) }
    }

    private fun CoroutineScope.launchConverters(
        convertersContext: ExecutorCoroutineDispatcher,
        ipStringChannel: ReceiveChannel<List<String>>
    ) = launch {
        supervisorScope {
            val jobsArray = Array(convertersNumber) {
                async(convertersContext) {
                    for (chunk in ipStringChannel) {
                        ipIntChannel.send(chunk.mapToArray(String::toIpInt))
                    }
                }
            }
            awaitAll(*jobsArray)
            ipIntChannel.close()
        }
    }

    private fun launchStorageWorkers(storageWorkersContext: ExecutorCoroutineDispatcher) = runBlocking {
        supervisorScope {
            val jobsArray = Array(storageWorkersNumber) {
                async(storageWorkersContext) {
                    for (chunk in ipIntChannel) {
                        for (ip in chunk) {
                            storage += ip
                        }
                    }
                }
            }
            awaitAll(*jobsArray)
        }
    }
}