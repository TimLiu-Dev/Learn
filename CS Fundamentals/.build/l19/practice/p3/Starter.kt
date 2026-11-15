data class Member(var name: String, val city: String)

class Registry {
    private val points = HashMap<Member, Int>()
    val size: Int get() = points.size

    fun add(member: Member, pts: Int) {
        points[member] = pts
    }

    fun pointsOf(member: Member): Int? = points[member]

    // Gives the member a new name and keeps their points. Returns the renamed member, or null if unknown.
    fun rename(member: Member, newName: String): Member? {
        if (member !in points) return null
        member.name = newName          // changes the key while it's inside the map
        return member
    }
}
