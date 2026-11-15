/** The next SemVer release after [current], given the commit messages since it, or null if there's nothing to release. */
fun nextVersion(current: String, commits: List<String>): String? {
    TODO("parse current, find the biggest change in commits, bump that number")
}
