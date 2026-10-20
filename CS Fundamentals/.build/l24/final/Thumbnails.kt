import java.util.Random

// A thumbnail service: requests arrive in batches, thumbnails are cached, and a
// report counts the distinct images per batch. It has a leak and a hotspot.
object Thumbnails {
    val cache = HashMap<String, ByteArray>()                   // shared by all requests

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
