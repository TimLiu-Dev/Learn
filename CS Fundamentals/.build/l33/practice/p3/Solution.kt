data class Note(val id: Int, val owner: String, val text: String)

val tokens = mapOf("tok-ana" to "ana", "tok-ben" to "ben")
val notes = mapOf(
    1 to Note(1, "ana", "Ana's plan"),
    2 to Note(2, "ben", "Ben's list"),
)

// Returns (status, body).
fun readNote(token: String?, id: Int): Pair<Int, String> {
    val caller = tokens[token] ?: return 401 to "who are you?"      // who is asking, before anything else
    val note = notes[id]
    // Deny by default: only an exact owner match gets through.
    if (note == null || note.owner != caller) return 404 to "not found"
    return 200 to note.text
}
