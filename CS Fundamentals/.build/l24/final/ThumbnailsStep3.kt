import java.util.Random

// Step 3: the cache is now a bounded LRU. The hotspot is still there.
object Thumbnails {
    const val MAX_ENTRIES = 2_000                              // enough for one batch's images
    val cache = object : LinkedHashMap<String, ByteArray>(16, 0.75f, true) { // LRU (L19)
        override fun removeEldestEntry(eldest: MutableMap.MutableEntry<String, ByteArray>) =
            size > MAX_ENTRIES
    }

    fun render(id: String): ByteArray {                        // pretend to resize an image
        val pixels = ByteArray(1_000)
        pixels[0] = id.hashCode().toByte()
        return pixels
    }

    fun thumbnail(id: String): ByteArray = cache.getOrPut(id) { render(id) }

    fun distinct(ids: List<String>): List<String> {            // keeps first-seen order
        val out = ArrayList<String>()
        for (id in ids) {
            if (id !in out) out.add(id)
        }
        return out
    }
}

fun main() {
    val random = Random(42)
    val start = System.nanoTime()
    var checksum = 0L
    for (batch in 1..1_000) {
        val requests = List(4_000) {                           // new images keep arriving
            "img${batch * 1_000 + random.nextInt(3_000)}"
        }
        for (id in requests) checksum += Thumbnails.thumbnail(id)[0]
        val unique = Thumbnails.distinct(requests).size
        if (batch % 100 == 0) {
            println("batch $batch: $unique distinct, cache ${Thumbnails.cache.size}")
        }
    }
    println("checksum $checksum, ${(System.nanoTime() - start) / 1_000_000} ms")
}
