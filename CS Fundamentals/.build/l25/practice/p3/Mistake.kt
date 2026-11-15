private fun parts(version: String): List<Int> = version.split(".").map { it.toInt() }

// Mistake: compares the text, so "1.9.0" beats "1.10.0".
fun compareVersions(a: String, b: String): Int = a.compareTo(b)

fun resolve(requests: List<String>): Map<String, String> =
    requests
        .groupBy({ it.substringBeforeLast(":") }, { it.substringAfterLast(":") })
        .mapValues { (_, versions) -> versions.maxWith(::compareVersions) }
        .toSortedMap()

fun isBreaking(from: String, to: String): Boolean {
    val (old, new) = parts(from) to parts(to)
    if (old[0] == 0) return from != to
    return new[0] != old[0]
}
