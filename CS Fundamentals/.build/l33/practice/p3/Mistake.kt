data class Note(val id: Int, val owner: String, val text: String)

val tokens = mapOf("tok-ana" to "ana", "tok-ben" to "ben")
val notes = mapOf(
    1 to Note(1, "ana", "Ana's plan"),
    2 to Note(2, "ben", "Ben's list"),
)

// Returns (status, body).
// Checks that the caller is logged in, but not that the note is theirs.
fun readNote(token: String?, id: Int): Pair<Int, String> {
    tokens[token] ?: return 401 to "who are you?"
    val note = notes[id] ?: return 404 to "not found"
    return 200 to note.text
}
