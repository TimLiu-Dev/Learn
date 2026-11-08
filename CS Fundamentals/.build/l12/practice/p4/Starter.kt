class Repo {
    val parents = mutableMapOf("c1" to listOf<String>())   // commit id -> its parents (a merge commit has two)
    val branches = mutableMapOf("main" to "c1")            // branch name -> the commit it points at
    var head = "main"                                      // the branch you're on
    private var made = 1

    private fun newCommit(vararg from: String): String {
        val id = "c${++made}"
        parents[id] = from.toList()
        return id
    }

    fun commit(): String = newCommit(branches.getValue(head)).also { branches[head] = it }
    fun createBranch(name: String) { branches[name] = branches.getValue(head) }
    fun switch(name: String) { head = name }

    // Is `a` reachable from `b` by following parent links? (a == b counts.)
    fun isAncestor(a: String, b: String): Boolean = TODO("follow every parent of b")

    // Merges branch `other` into the current branch. Returns "up to date", "fast-forward" or "merge commit".
    fun merge(other: String): String {
        val ours = branches.getValue(head)
        val theirs = branches.getValue(other)
        TODO("decide which of the three cases this is, then move the pointer")
    }
}
