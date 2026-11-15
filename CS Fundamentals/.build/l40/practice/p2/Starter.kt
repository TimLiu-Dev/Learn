data class Commit(val hash: String, val subject: String)

/** The todo list `git rebase -i --autosquash` starts from. Commits are oldest first. */
fun autosquash(commits: List<Commit>): List<String> {
    TODO("start with every commit as a pick line, then move the fixup! and squash! ones")
}
