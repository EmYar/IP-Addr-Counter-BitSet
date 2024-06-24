package me.emyar.runners

import kotlinx.coroutines.*
import kotlinx.coroutines.channels.Channel.Factory.BUFFERED
import kotlinx.coroutines.channels.produce
import me.emyar.Storage
import me.emyar.toIpInt
import java.io.File

@OptIn(DelicateCoroutinesApi::class, ExperimentalCoroutinesApi::class)
class MultithreadingRun(
    private val inputFile: File,
    private val convertersNumber: Int = 2,
    private val chunkSize: Int = 2048
) : Runner {

    override fun run(): Long = runBlocking {
        newSingleThreadContext("FileReader").use { fileReaderContext ->
            newFixedThreadPoolContext(convertersNumber, "IpsConverter").use { convertersContext ->
                newSingleThreadContext("StorageWorker").use { storageWorkerContext ->
                    doRun(fileReaderContext, convertersContext, storageWorkerContext)
                }
            }
        }
    }

    private suspend fun CoroutineScope.doRun(
        fileReaderContext: CoroutineDispatcher,
        convertersContext: CoroutineDispatcher,
        storageWorkerContext: CoroutineDispatcher,
    ): Long {
        val storage = Storage()

        val ipStringChannel = produce(fileReaderContext, BUFFERED) {
            inputFile.useLines {
                it.chunked(chunkSize)
                    .forEach { send(it) }
            }
        }

        val ipIntChannel = produce(capacity = BUFFERED) {
            Array(convertersNumber) {
                launch(convertersContext) {
                    for (stringsChunk in ipStringChannel) {
                        send(stringsChunk.toIpsIntArray())
                    }
                }
            }.forEach { it.join() }
        }

        launch(storageWorkerContext) {
            for (chunk in ipIntChannel) {
                for (ip in chunk) {
                    storage += ip
                }
            }
        }.join()

        return storage.uniqueIpsCount
    }

    private fun List<String>.toIpsIntArray(): IntArray =
        IntArray(size) { index ->
            this[index].toIpInt()
        }
}