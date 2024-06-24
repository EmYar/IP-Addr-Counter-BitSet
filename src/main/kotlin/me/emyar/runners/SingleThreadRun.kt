package me.emyar.runners

import me.emyar.Storage
import me.emyar.toIpInt
import java.io.File

class SingleThreadRun(private val inputFile: File) : Runner {

    override fun run(): Long {
        val storage = Storage()

        inputFile.forEachLine { storage += it.toIpInt() }

        return storage.uniqueIpsCount
    }
}