fun nextVersion(current: String, commits: List<String>): String? {
    require(Regex("""\d+\.\d+\.\d+""").matches(current)) { "not a MAJOR.MINOR.PATCH version: $current" }
    val (major, minor, patch) = current.split(".").map { it.toInt() }
    val header = Regex("""^(\w+)(\([^)]*\))?(!)?: """)
    var breaking = false
    var feat = false
    var fix = false
    for (message in commits) {
        val lines = message.lines()
        val m = header.find(lines.first())
        if (m?.groupValues?.get(3) == "!" ||
            lines.drop(1).any { it.startsWith("BREAKING CHANGE:") || it.startsWith("BREAKING-CHANGE:") }) breaking = true
        when (m?.groupValues?.get(1)) {
            "feat" -> feat = true
            "fix" -> fix = true
        }
    }
    // Bumps one number but forgets to reset the ones to its right.
    return when {
        breaking && major == 0 -> "0.${minor + 1}.$patch"
        breaking -> "${major + 1}.$minor.$patch"
        feat -> "$major.${minor + 1}.$patch"
        fix -> "$major.$minor.${patch + 1}"
        else -> null
    }
}
