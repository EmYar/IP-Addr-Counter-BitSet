package me.emyar

import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertAll
import kotlin.test.assertEquals

class Converter {

    @Test
    fun toIpUInt() {
        assertAll(
            { assertEquals(ip0_0_0_0, "0.0.0.0".toIpInt()) },
            { assertEquals(ip192_168_0_0, "192.168.0.0".toIpInt()) },
            { assertEquals(ip192_168_0_1, "192.168.0.1".toIpInt()) },
            { assertEquals(ip127_255_255_255, "127.255.255.255".toIpInt()) },
            { assertEquals(ip255_255_255_254, "255.255.255.254".toIpInt()) },
            { assertEquals(ip255_255_255_255, "255.255.255.255".toIpInt()) }
        )
    }

    @Test
    fun toIpString() {
        assertAll(
            { assertEquals("0.0.0.0", ip0_0_0_0.toStringIp()) },
            { assertEquals("192.168.0.0", ip192_168_0_0.toStringIp()) },
            { assertEquals("192.168.0.1", ip192_168_0_1.toStringIp()) },
            { assertEquals("127.255.255.255", ip127_255_255_255.toStringIp()) },
            { assertEquals("255.255.255.254", ip255_255_255_254.toStringIp()) },
            { assertEquals("255.255.255.255", ip255_255_255_255.toStringIp()) },
        )
    }
}