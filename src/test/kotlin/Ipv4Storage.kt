import me.emyar.Ipv4Storage
import org.junit.jupiter.api.Test
import kotlin.test.assertEquals

class Ipv4Storage {

    @Test
    fun add() {
        val storage = Ipv4Storage()
        storage.add(ip192_168_0_0)
        storage.add(ip192_168_0_1)
        storage.add(ip192_168_0_0)
        assertEquals(2, storage.uniqueIpsCount)
    }
}