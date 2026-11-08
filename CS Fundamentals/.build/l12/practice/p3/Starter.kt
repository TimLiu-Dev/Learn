class Repo {
    val parent = mutableMapOf<String, String?>("c1" to null)   // commit id -> its parent (null for the first)
    val branches = mutableMapOf("main" to "c1")                 // branch name -> the commit it points at
    var head = "main"                                           // the branch you're on
    private var made = 1

    // A new commit whose parent is the current commit. Returns its id: c2, c3, ...
    fun commit(): String {
        val id = "c${++made}"
        parent[id] = branches.getValue(head)
        TODO("move the right pointer, then return id")
    }

    // Like `git branch name`: a new pointer at the current commit. HEAD doesn't move.
    fun createBranch(name: String) {
        TODO("refuse an existing name, then add one pointer")
    }

    // Like `git switch name`.
    fun switch(name: String) {
        TODO("refuse an unknown name, then move HEAD")
    }

    // Like `git log --oneline`: newest first.
    fun log(): List<String> = TODO("follow parent links from HEAD's commit")
}
