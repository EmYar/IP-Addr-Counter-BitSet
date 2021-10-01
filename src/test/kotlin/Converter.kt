import me.emyar.toIpInt
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertAll
import kotlin.test.assertEquals

class Converter {

    @Test
    fun toIpInt() {
        assertAll(
            { assertEquals(ip0_0_0_0, "0.0.0.0".toIpInt()) },
            { assertEquals(ip192_168_0_0, "192.168.0.0".toIpInt()) },
            { assertEquals(ip192_168_0_1, "192.168.0.1".toIpInt()) },
            { assertEquals(ip255_255_255_255, "255.255.255.255".toIpInt()) }
        )
    }
}