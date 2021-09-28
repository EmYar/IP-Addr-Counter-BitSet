package me.emyar

class Ipv4Storage {

    private val storage = LargeBitSet(4_294_967_296u)

    private var _uniqueIpsCount = 0L
    val uniqueIpsCount get() = _uniqueIpsCount

    fun add(ip: UInt) {
        val ipLong = ip.toULong()
        if (!storage.get(ipLong)) {
            storage.set(ipLong)
            _uniqueIpsCount++
        }
    }
}