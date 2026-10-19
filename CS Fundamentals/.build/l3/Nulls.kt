fun emailDomain(contacts: Map<String, String?>, name: String): String? =
    contacts[name]?.substringAfter('@')

fun main() {
    val contacts = mapOf("ada" to "ada@math.org", "linus" to null, "grace" to "grace@navy.mil")

    for (name in listOf("ada", "linus", "grace", "alan")) {
        val domain = emailDomain(contacts, name)
        println("$name: ${domain ?: "(no email)"}")
    }

    val email = contacts["ada"]          // type: String?
    if (email != null) {
        println(email.length)            // smart cast: String inside this block
    }
    contacts["grace"]?.let { println("grace's address has ${it.length} chars") }

    val n = "42x".toIntOrNull() ?: -1
    println(n)
    println(contacts["alan"]!!.length)   // "trust me, it's not null"... it is
}
