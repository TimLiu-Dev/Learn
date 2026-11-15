enum class Action { READ, EDIT, DELETE, SHARE }

data class User(val name: String, val admin: Boolean = false)
data class Note(val owner: String, val isPublic: Boolean = false)

// Lists who is refused, and lets everything else through.
fun decide(user: User?, action: Action, note: Note?): Int = when {
    note == null -> 404
    action == Action.READ && note.isPublic -> 200
    user == null -> 401
    user.name != note.owner && !user.admin -> 403
    else -> 200
}
