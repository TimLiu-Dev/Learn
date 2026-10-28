import java.nio.ByteBuffer
import java.security.MessageDigest
import java.util.TreeMap

// A well-mixed 64-bit hash of a string: the first 8 bytes of its MD5 digest.
fun hash64(s: String): Long = ByteBuffer.wrap(MessageDigest.getInstance("MD5").digest(s.toByteArray())).long

fun modShard(key: String, n: Int) = "n${Math.floorMod(hash64(key), n) + 1}"

// Consistent hashing: nodes and keys share one ring of 64-bit positions.
class Ring(private val vnodes: Int) {
    private val ring = TreeMap<Long, String>()                        // ring position -> node
    fun add(node: String) = repeat(vnodes) { ring[hash64("$node#$it")] = node }
    fun remove(node: String) = repeat(vnodes) { ring.remove(hash64("$node#$it")) }
    // Walk clockwise from the key's position to the first node position (wrap around at the end).
    fun nodeFor(key: String): String = (ring.ceilingEntry(hash64(key)) ?: ring.firstEntry()).value
}

fun main() {
    val keys = (0 until 100_000).map { "user:$it" }

    val moved = keys.count { modShard(it, 4) != modShard(it, 5) }
    println("hash mod N, 4 -> 5 nodes: %,d of %,d keys move (%.0f%%)".format(moved, keys.size, 100.0 * moved / keys.size))

    for (vnodes in listOf(1, 100)) {
        val ring = Ring(vnodes)
        listOf("n1", "n2", "n3", "n4").forEach(ring::add)
        val before = keys.associateWith(ring::nodeFor)
        val load = before.values.groupingBy { it }.eachCount().toSortedMap()
        ring.add("n5")
        val after = keys.associateWith(ring::nodeFor)
        val changed = keys.filter { before[it] != after[it] }
        println("ring, %3d vnode(s) per node: load %s".format(vnodes, load))
        println("   add n5: %,d keys move (%.0f%%), all of them to n5: %s".format(
            changed.size, 100.0 * changed.size / keys.size, changed.all { after[it] == "n5" }))
    }
}
