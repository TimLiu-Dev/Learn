class Server(val name: String) {
    private val visits = mutableMapOf<String, Int>()     // kept in this server's memory

    fun visit(user: String): String {
        val n = (visits[user] ?: 0) + 1
        visits[user] = n
        return "$name$n"
    }
}

fun main() {
    val servers = listOf(Server("A"), Server("B"))
    var next = 0
    val replies = List(3) { servers[next++ % servers.size].visit("ada") }   // round-robin
    println(replies.joinToString(" "))
}
