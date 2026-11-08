class Repo {
    val parent = mutableMapOf<String, String?>("c1" to null)   // commit id -> its parent (null for the first)
    val branches = mutableMapOf("main" to "c1")                 // branch name -> the commit it points at
    var head = "main"                                           // the branch you're on
    private var made = 1

    // A new commit whose parent is the current commit. Returns its id: c2, c3, ...
    fun commit(): String {
        val id = "c${++made}"
        val old = branches.getValue(head)
        parent[id] = old
        for ((name, at) in branches) if (at == old) branches[name] = id   // moves every branch "on" that commit
        return id
    }

    // Like `git branch name`: a new pointer at the current commit. HEAD doesn't move.
    fun createBranch(name: String) {
        require(name !in branches) { "a branch named '$name' already exists" }
        branches[name] = branches.getValue(head)
    }

    // Like `git switch name`.
    fun switch(name: String) {
        require(name in branches) { "invalid reference: $name" }
        head = name
    }

    // Like `git log --oneline`: newest first.
    fun log(): List<String> = generateSequence(branches.getValue(head)) { parent[it] }.toList()
}
