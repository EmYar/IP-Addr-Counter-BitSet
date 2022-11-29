package me.emyar

import kotlinx.coroutines.*
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.channels.ReceiveChannel
import kotlinx.coroutines.channels.produce
import java.io.BufferedReader
import java.nio.file.Path

@OptIn(ExperimentalUnsignedTypes::class, DelicateCoroutinesApi::class)
class MultithreadingRun(
    private val inputFilePath: Path,
    private val convertersNumber: Int = 3,
    private val storageWorkersNumber: Int = 2,
    private val chunkSize: Int = 4096,
    private val bufferSize: Int = 64,
) {

    fun run(): Long = runBlocking {
        newSingleThreadContext("FileReader").use { fileReaderContext ->
            newFixedThreadPoolContext(convertersNumber, "IpsConverter").use { convertersContext ->
                newFixedThreadPoolContext(storageWorkersNumber, "StorageWorker").use { storageWorkersContext ->
                    inputFilePath.toFile().bufferedReader().use { reader ->
                        val storage = Ipv4Set()
                        val ipStringChannel = launchFileReader(fileReaderContext, reader)
                        val ipIntChannel = launchConverters(convertersContext, ipStringChannel)
                        launchStorageWorkers(storageWorkersContext, ipIntChannel, storage)
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
    ): Channel<UIntArray> {
        val ipIntChannel = Channel<UIntArray>(bufferSize)
        launch {
            supervisorScope {
                launchAsyncMultipleAndAwait(convertersContext, convertersNumber) {
                    for (chunk in ipStringChannel)
                        ipIntChannel.send(chunk.mapToArray(String::toIpInt))
                }
                ipIntChannel.close()
            }
        }
        return ipIntChannel
    }

    private suspend fun launchStorageWorkers(
        storageWorkersContext: ExecutorCoroutineDispatcher,
        ipIntChannel: Channel<UIntArray>,
        storage: Ipv4Set
    ) =
        supervisorScope {
            launchAsyncMultipleAndAwait(storageWorkersContext, storageWorkersNumber) {
                for (chunk in ipIntChannel)
                    for (ip in chunk)
                        storage.add(ip)
            }
        }

    private suspend fun launchAsyncMultipleAndAwait(
        context: ExecutorCoroutineDispatcher,
        count: Int,
        task: suspend () -> Unit
    ) = coroutineScope {
        awaitAll(*Array(count) { async(context) { task() } })
    }
}