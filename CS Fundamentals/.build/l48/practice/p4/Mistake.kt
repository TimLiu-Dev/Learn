// A 64-bit Bloom filter: each item sets 3 of the 64 bits in one Long.
class TinyBloom {
    var bits = 0L
        private set

    // Three bit positions (0..63), cut from different parts of the hash code.
    fun positions(item: String): List<Int> {
        val h = item.hashCode()
        return listOf(h and 63, (h ushr 6) and 63, (h ushr 12) and 63)
    }

    fun add(item: String) {
        for (i in positions(item)) bits = bits or (1 shl i).toLong()    // an Int shift, widened too late
    }

    fun mightContain(item: String): Boolean = positions(item).all { i -> bits and (1L shl i) != 0L }
}
