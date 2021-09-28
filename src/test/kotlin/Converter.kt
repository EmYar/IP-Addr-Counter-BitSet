import me.emyar.storeIpAsInt
import me.emyar.toIntIpsArray
import org.junit.jupiter.api.Test
import kotlin.test.assertContentEquals
import kotlin.test.assertEquals

class Converter {

    @ExperimentalUnsignedTypes
    @Test
    fun toIntIpsArray() {
        val stringsArray = arrayOf("192.168.0.0", "192.168.0.1", null)
        assertContentEquals(uintArrayOf(ip192_168_0_0, ip192_168_0_1), stringsArray.toIntIpsArray())
    }

    @Test
    fun storeIpAsInt() {
        assertEquals(ip192_168_0_0, "192.168.0.0".storeIpAsInt())
    }
}