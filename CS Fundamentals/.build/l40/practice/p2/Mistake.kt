data class Commit(val hash: String, val subject: String)

fun autosquash(commits: List<Commit>): List<String> {
    val todo = mutableListOf<String>()
    for (c in commits) {
        val action = listOf("fixup", "squash").firstOrNull { c.subject.startsWith("$it! ") }
        val at = action?.let { a -> todo.indexOfFirst { it.endsWith(" # " + c.subject.removePrefix("$a! ")) } } ?: -1
        // Inserts right after the target, so a second fixup lands in front of the first.
        if (action != null && at >= 0) todo.add(at + 1, "$action ${c.hash} # ${c.subject}")
        else todo.add("pick ${c.hash} # ${c.subject}")
    }
    return todo
}
