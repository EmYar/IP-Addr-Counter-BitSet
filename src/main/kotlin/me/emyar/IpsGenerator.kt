package me.emyar

import java.nio.file.Files
import java.nio.file.Paths
import java.nio.file.attribute.BasicFileAttributes
import kotlin.random.Random
import kotlin.system.measureTimeMillis

private const val UNIQUE_IPS_BATCH_SIZE: Long = 10_000_000
private const val BATCHES_COUNT = 400
// 10_000_000 * 400 = ~52.3gb

fun main() {
    val fileDir = Paths.get(System.getProperty("user.home"), "IP-Addr-Counter")
        .toFile()
    val file = fileDir.resolve("ip_addresses")

    if (!fileDir.exists()) {
        fileDir.mkdirs()
    }

    if (file.exists()) {
        file.delete()
    }
    file.createNewFile()

    println("File path: ${file.path}")

    val storage = Storage()

    val timeToGenerateIps = measureTimeMillis {
        while (storage.uniqueIpsCount < UNIQUE_IPS_BATCH_SIZE) {
            storage += Random.nextInt()
        }
    }
    printMeasuredTime("$UNIQUE_IPS_BATCH_SIZE unique IPs generated in", timeToGenerateIps)

    val timeToWriteFile = measureTimeMillis {
        file.bufferedWriter().use { writer ->
            repeat(BATCHES_COUNT) {
                storage.stream()
                    .forEach { writer.appendLine(it.toStringIp()) }
            }
        }
    }

    val fileAttributes = Files.readAttributes(file.toPath(), BasicFileAttributes::class.java)
    val fileSizeMb = fileAttributes.size() / 1024 / 1024
    printMeasuredTime(
        "File with ${UNIQUE_IPS_BATCH_SIZE * BATCHES_COUNT} IPs and ${fileSizeMb}mb generated in",
        timeToWriteFile
    )
}