// "grace hopper" -> "GH": one upper-case letter per word.
fun initials(name: String): String {
    val words = name.split(" ").filter { it.isNotEmpty() }   // stray spaces make empty "words"
    return words.joinToString("") { it[0].uppercase() }
}

fun badges(roster: List<String>): List<String> = roster.map { initials(it) }
