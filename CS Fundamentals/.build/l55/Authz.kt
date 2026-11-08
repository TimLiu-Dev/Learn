// Authorization: one function decides, every request asks it, and anything it doesn't know is denied.
enum class Role { READER, EDITOR, ADMIN }
enum class Action { READ, CREATE, UPDATE, DELETE, PUBLISH }

data class User(val name: String, val roles: Set<Role>)
data class Book(val id: Long, val owner: String)

// RBAC: roles grant permissions. "own" permissions also need the relationship check below.
val permissions: Map<Role, Set<String>> = mapOf(
    Role.READER to setOf("book:read"),
    Role.EDITOR to setOf("book:read", "book:create", "book:update:own", "book:delete:own"),
    Role.ADMIN to setOf("book:read", "book:create", "book:update:any", "book:delete:any"),
)

fun can(user: User?, action: Action, book: Book?): Boolean {
    if (action == Action.READ) return true                            // the catalogue is public
    if (user == null) return false                                    // everything else needs a login
    val granted = user.roles.flatMap { permissions[it].orEmpty() }.toSet()
    val verb = action.name.lowercase()
    return when {
        "book:$verb:any" in granted -> true
        "book:$verb:own" in granted -> book != null && book.owner == user.name   // ownership, not just role
        "book:$verb" in granted -> true
        else -> false                                                 // no rule matched: deny by default
    }
}

fun main() {
    val anasBook = Book(1, owner = "ana")
    val people = listOf(
        null,
        User("ben", setOf(Role.READER)),
        User("ana", setOf(Role.EDITOR)),
        User("cy", setOf(Role.EDITOR)),
        User("root", setOf(Role.ADMIN)),
    )
    println("on ana's book   " + Action.entries.joinToString("") { it.name.padEnd(9) })
    for (u in people) {
        val label = u?.let { "${it.name} (${it.roles.first().name.lowercase()})" } ?: "anonymous"
        println(label.padEnd(16) + Action.entries.joinToString("") { (if (can(u, it, anasBook)) "yes" else "-").padEnd(9) })
    }
}
