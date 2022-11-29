import me.emyar.ThreadSyncIpv4Set
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertDoesNotThrow
import kotlin.test.assertEquals

class Ipv4Set {

    @Test
    fun add() {
        assertDoesNotThrow {
            ThreadSyncIpv4Set().apply {
                add(ip192_168_0_0)
                add(ip192_168_0_0)
                add(ip192_168_0_1)
                add(ip127_255_255_255)
                add(ip255_255_255_254)
                add(ip255_255_255_255)
            }
        }
    }

    @Test
    fun getUniqueIpsCount() {
        val storage = ThreadSyncIpv4Set().apply {
            add(ip192_168_0_0)
            add(ip192_168_0_0)
            add(ip192_168_0_1)
            add(ip127_255_255_255)
            add(ip255_255_255_254)
            add(ip255_255_255_255)
        }
        assertEquals(5, storage.uniqueIpsCount)
    }
}