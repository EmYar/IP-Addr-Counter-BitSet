package me.emyar.runners

import me.emyar.Storage
import me.emyar.toIpInt
import java.nio.file.Path

class SingleThreadRun(private val inputFilePath: Path) : Runner {

    override fun run(): Long {
        val storage = Storage()

        inputFilePath.toFile().bufferedReader().use { reader ->
            reader.forEachLine { storage += it.toIpInt() }
        }

        return storage.uniqueIpsCount
    }
}