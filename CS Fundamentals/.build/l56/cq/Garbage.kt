fun main() {
    val log = mutableListOf<String>()
    val index = HashMap<String, Int>()
    fun put(k: String, v: String) { log += "put $k $v"; index[k] = log.lastIndex }
    fun delete(k: String) { log += "del $k"; index.remove(k) }

    put("a", "1"); put("b", "2"); put("a", "3"); delete("b")
    println("${log.size} records, ${index.size} live, a at ${index["a"]}")
}
