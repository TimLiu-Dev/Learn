enum class Action { READ, EDIT, DELETE, SHARE }

data class User(val name: String, val admin: Boolean = false)
data class Note(val owner: String, val isPublic: Boolean = false)

// Every request asks this one function. user is null when nobody is logged in; note is null when it doesn't exist.
fun decide(user: User?, action: Action, note: Note?): Int = when {
    note == null -> 404
    action == Action.READ && note.isPublic -> 200                          // public on purpose, and said so
    user == null -> 401                                                    // who are you? (authentication)
    user.name == note.owner && action in setOf(Action.READ, Action.EDIT, Action.DELETE) -> 200
    user.admin && action in setOf(Action.READ, Action.DELETE) -> 200       // least privilege: no EDIT
    else -> 403                                                            // no rule matched: deny by default
}
