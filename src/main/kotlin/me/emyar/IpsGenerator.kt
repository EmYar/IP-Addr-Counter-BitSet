package me.emyar

import java.nio.file.Paths
import kotlin.random.Random
import kotlin.random.nextUInt

fun main() {
    val file = Paths.get(System.getProperty("user.home"), "IP-Addr-Counter", "ip_addresses")
        .toFile()

    if (file.exists())
        file.delete()
    file.createNewFile()

    file.bufferedWriter().use { writer ->
        repeat(5000) {
            repeat(1_000_000) {
                writer.appendLine(Random.nextUInt(0u, 4_294_967_295u).toStringIp())
            }
        }
//        val millionIps = LinkedHashSet<String>(1_000_000)
//        while (millionIps.size != 1_000_000) {
//            millionIps += Random.nextUInt(0u, 4_294_967_295u).toStringIp()
//        }
//        repeat(5000) {
//            millionIps.forEach(writer::appendLine)
//        }
    }
}

private fun UInt.toStringIp(): String =
    "${this.shr(24) and 255u}.${this.shr(16) and 255u}.${this.shr(8) and 255u}.${this and 255u}"