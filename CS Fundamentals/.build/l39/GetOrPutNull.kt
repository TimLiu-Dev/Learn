fun main() {
    val cache = HashMap<String, String?>()
    var lookups = 0
    fun slowFind(isbn: String): String? { lookups++; return null }   // this book doesn't exist

    repeat(3) { cache.getOrPut("9780000000000") { slowFind("9780000000000") } }
    println("getOrPut: 3 calls, $lookups slow lookups")              // null counts as "absent"
}
