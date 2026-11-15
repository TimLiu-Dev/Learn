fun main() {
    val m = mutableMapOf("c" to 1, "a" to 2, "b" to 3)
    m["c"] = 10          // a key that's already there
    m.remove("a")
    m["a"] = 20          // removed, then put back
    println(m.keys)
}
