data class Member(val name: String, val city: String)   // val: a key's hash can never change

class Registry {
    private val points = HashMap<Member, Int>()
    val size: Int get() = points.size

    fun add(member: Member, pts: Int) {
        points[member] = pts
    }

    fun pointsOf(member: Member): Int? = points[member]

    // Gives the member a new name and keeps their points. Returns the renamed member, or null if unknown.
    fun rename(member: Member, newName: String): Member? {
        val pts = points.remove(member) ?: return null   // take the entry out under its old hash
        val renamed = member.copy(name = newName)
        points[renamed] = pts                            // and file it again under the new one
        return renamed
    }
}
