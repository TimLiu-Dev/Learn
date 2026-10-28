// Sticky routing: the same client always goes to the same backend.
fun pick(client: String, backends: List<String>) = backends[Math.floorMod(client.hashCode(), backends.size)]

fun main() {
    val clients = listOf("ada", "bob", "cy", "dee", "eve", "fay", "gus", "hal", "ivy", "jo")
    val before = clients.associateWith { pick(it, listOf("b1", "b2", "b3")) }
    val after = clients.associateWith { pick(it, listOf("b1", "b3")) }          // b2 died
    for (c in clients) {
        val note = when {
            before[c] == after[c] -> ""
            before[c] == "b2" -> "  moved (its backend died)"
            else -> "  MOVED although its backend is fine"
        }
        println("%-4s %s -> %s%s".format(c, before[c], after[c], note))
    }
    val needless = clients.count { before[it] != after[it] && before[it] != "b2" }
    println("clients that lost their session for no reason: $needless of ${clients.size}")
}
