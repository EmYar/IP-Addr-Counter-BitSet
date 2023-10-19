package me.emyar.runners

import kotlinx.coroutines.*
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.channels.ReceiveChannel
import kotlinx.coroutines.channels.produce
import me.emyar.Storage
import me.emyar.toIpInt
import java.io.BufferedReader
import java.nio.file.Path

@OptIn(DelicateCoroutinesApi::class, ExperimentalCoroutinesApi::class)
class MultithreadingRun(
    private val inputFilePath: Path,
    private val convertersNumber: Int = 2,
    private val chunkSize: Int = 1024,
    private val bufferSize: Int = convertersNumber * 2,
) : Runner {

    override fun run(): Long = runBlocking {
        newSingleThreadContext("FileReader").use { fileReaderContext ->
            newFixedThreadPoolContext(convertersNumber, "IpsConverter").use { convertersContext ->
                newSingleThreadContext("StorageWorker").use { storageWorkerContext ->
                    inputFilePath.toFile().bufferedReader().use { reader ->
                        val storage = Storage()
                        val ipStringChannel = launchFileReader(fileReaderContext, reader)
                        val ipIntsChannel = launchConverters(convertersContext, ipStringChannel)
                        launchStorageWorker(storageWorkerContext, ipIntsChannel, storage)
                            .join()
                        storage.uniqueIpsCount
                    }
                }
            }
        }
    }

    private fun CoroutineScope.launchFileReader(
        fileReaderContext: CoroutineDispatcher,
        reader: BufferedReader
    ): ReceiveChannel<Array<String>> {
        Thread.currentThread().priority = Thread.MAX_PRIORITY
        var chunk = Array(chunkSize) { "" }
        var chunkPosition = 0
        return produce(fileReaderContext, bufferSize) {
            reader.lineSequence()
                .forEach {
                    chunk[chunkPosition++] = it
                    if (chunkPosition == chunkSize) {
                        send(chunk)
                        chunk = Array(chunkSize) { "" }
                        chunkPosition = 0
                    }
                }
            // send remain strings
            if (chunkPosition > 0) {
                @Suppress("UNCHECKED_CAST")
                send(chunk.copyOf(chunkPosition) as Array<String>)
            }
        }
    }

    private fun CoroutineScope.launchConverters(
        convertersContext: CoroutineDispatcher,
        ipStringChannel: ReceiveChannel<Array<String>>
    ): Channel<IntArray> {
        val convertedIpsChannel = Channel<IntArray>(bufferSize)
        launch {
            supervisorScope {
                launchMultipleAsyncJobsAndWaitAll(convertersContext, convertersNumber) {
                    for (stringsChunk in ipStringChannel) {
                        val ipsChunk = IntArray(stringsChunk.size)
                        for ((ipsChunkPosition, ipString) in stringsChunk.withIndex()) {
                            val ipUInt = ipString.toIpInt()
                            ipsChunk[ipsChunkPosition] = ipUInt
                        }
                        convertedIpsChannel.send(ipsChunk)
                    }
                }
                convertedIpsChannel.close()
            }
        }
        return convertedIpsChannel
    }

    private fun CoroutineScope.launchStorageWorker(
        context: CoroutineDispatcher,
        channel: Channel<IntArray>,
        storage: Storage
    ) = async(context) {
        for (chunk in channel) {
            for (ip in chunk) {
                storage += ip
            }
        }
    }

    private suspend fun launchMultipleAsyncJobsAndWaitAll(
        context: CoroutineDispatcher,
        count: Int,
        job: suspend () -> Unit
    ) {
        coroutineScope {
            awaitAll(*Array(count) { async(context) { job() } })
        }
    }
}