data class Commit(val hash: String, val subject: String)

/** The todo list `git rebase -i --autosquash` starts from. Commits are oldest first. */
fun autosquash(commits: List<Commit>): List<String> {
    val groups = mutableListOf<MutableList<String>>()          // one group per picked commit, in order
    val bySubject = mutableMapOf<String, MutableList<String>>()
    for (c in commits) {
        val action = listOf("fixup", "squash").firstOrNull { c.subject.startsWith("$it! ") }
        val target = action?.let { bySubject[c.subject.removePrefix("$it! ")] }
        if (action != null && target != null) {
            target.add("$action ${c.hash} # ${c.subject}")      // joins the end of its target's group
        } else {
            val group = mutableListOf("pick ${c.hash} # ${c.subject}")
            groups.add(group)
            bySubject.putIfAbsent(c.subject, group)
        }
    }
    return groups.flatten()
}
