class Store {
    private var highest = ""                  // tokens kept as strings
    var value = "-"
    fun write(token: String, v: String): Boolean {
        if (token < highest) return false
        highest = token; value = v
        return true
    }
}

fun main() {
    val s = Store()
    println("${s.write("9", "A")} ${s.write("10", "B")} ${s.write("9", "C")} ${s.value}")
}
