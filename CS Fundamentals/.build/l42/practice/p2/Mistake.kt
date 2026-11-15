import java.util.TreeMap
import java.util.zip.CRC32

// A fixed position on the ring for any string: its CRC32, a number from 0 to 2^32 - 1.
fun position(s: String): Long = CRC32().apply { update(s.toByteArray()) }.value

class Ring(private val vnodes: Int) {
    private val ring = TreeMap<Long, String>()          // position -> node

    // Node "n1" with 3 vnodes sits at position("n1#0"), position("n1#1") and position("n1#2").
    fun add(node: String) = repeat(vnodes) { ring[position("$node#$it")] = node }
    fun remove(node: String) = repeat(vnodes) { ring.remove(position("$node#$it")) }

    // Clockwise from the key to the first node position.
    fun nodeFor(key: String): String = ring.ceilingEntry(position(key)).value          // forgets to wrap round
}
