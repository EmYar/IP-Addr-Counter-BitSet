package me.emyar

import kotlinx.coroutines.channels.SendChannel
import java.io.File

private const val ipsBuffer = 4096

suspend fun readFile(inputFilePath: String, sendChannel: SendChannel<Array<String?>>) {
    File(inputFilePath).bufferedReader().use { reader ->
        var array = Array<String?>(ipsBuffer) { null }
        var i = 0
        reader.lineSequence()
            .forEach {
                array[i++] = it
                if (i == array.size) {
                    sendChannel.send(array)
                    array = Array(ipsBuffer) { null }
                    i = 0
                }
            }
    }
}