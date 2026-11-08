// "grace hopper" -> "GH": one upper-case letter per word.
fun initials(name: String): String {
    val parts = name.replace("  ", " ").split(" ")
    var result = ""
    for (part in parts) result += part[0].uppercaseChar()
    return result
}

fun badges(roster: List<String>): List<String> = roster.map { initials(it) }
