enum class Action { READ, EDIT, DELETE, SHARE }

data class User(val name: String, val admin: Boolean = false)
data class Note(val owner: String, val isPublic: Boolean = false)

// Every request asks this one function. user is null when nobody is logged in; note is null when it doesn't exist.
fun decide(user: User?, action: Action, note: Note?): Int =
    TODO("404, public read, 401, owner, admin, then deny by default")
