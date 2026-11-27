/** The next SemVer release after [current], given the commit messages since it, or null if there's nothing to release. */
fun nextVersion(current: String, commits: List<String>): String? {
    require(Regex("""\d+\.\d+\.\d+""").matches(current)) { "not a MAJOR.MINOR.PATCH version: $current" }
    val (major, minor, patch) = current.split(".").map { it.toInt() }

    val header = Regex("""^(\w+)(\([^)]*\))?(!)?: """)       // type, optional (scope), optional !
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
    return when {
        breaking && major == 0 -> "0.${minor + 1}.0"     // 0.x: anything may change, so MINOR acts as MAJOR
        breaking -> "${major + 1}.0.0"
        feat -> "$major.${minor + 1}.0"
        fix -> "$major.$minor.${patch + 1}"
        else -> null
    }
}
