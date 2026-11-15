data class Member(val name: String, val city: String)

class Registry {
    private val points = HashMap<Member, Int>()
    val size: Int get() = points.size

    fun add(member: Member, pts: Int) {
        points[member] = pts
    }

    fun pointsOf(member: Member): Int? = points[member]

    // Puts a renamed copy in, but forgets to take the old key out.
    fun rename(member: Member, newName: String): Member? {
        val pts = points[member] ?: return null
        val renamed = member.copy(name = newName)
        points[renamed] = pts
        return renamed
    }
}
