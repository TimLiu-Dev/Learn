data class User(val name: String, val email: String?)

fun findUser(id: Int): User? = when (id) {
    1 -> User("ada", "ada@example.com")
    2 -> User("bob", null)
    else -> null
}

// Hard to read: three different things are all called `it`
fun domainNested(id: Int): String =
    findUser(id)?.let {
        it.email?.let {
            it.substringAfter('@').let {
                "domain $it"
            }
        }
    } ?: "no contact"

// The same logic with names and early returns
fun domain(id: Int): String {
    val user = findUser(id) ?: return "no contact"
    val email = user.email ?: return "no contact"
    return "domain ${email.substringAfter('@')}"
}

fun main() {
    for (id in 1..3) println("$id: ${domainNested(id)} | ${domain(id)}")
}
