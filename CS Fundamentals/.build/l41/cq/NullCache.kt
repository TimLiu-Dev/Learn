var loads = 0

fun findTitle(id: Int): String? {
    loads++
    return if (id == 1) "Dune" else null      // only book 1 exists
}

val cache = HashMap<Int, String?>()

fun title(id: Int): String? = cache.getOrPut(id) { findTitle(id) }

fun main() {
    repeat(3) { title(1) }
    repeat(3) { title(99) }
    println("loads $loads, keys ${cache.keys}")
}
