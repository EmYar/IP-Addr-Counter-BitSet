import me.emyar.LargeBitSet
import org.junit.jupiter.api.Test

class LargeBitSet {

    @Test
    fun set() {
        val bitSet = LargeBitSet(4_294_967_296u)
        val ip = ip192_168_0_0.toULong()
        bitSet.set(ip)
        assert(bitSet.get(ip))
    }
}