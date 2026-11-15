private fun parts(version: String): List<Int> = version.split(".").map { it.toInt() }

/** Compares "MAJOR.MINOR.PATCH" numerically: negative if a < b, 0 if equal, positive if a > b. */
fun compareVersions(a: String, b: String): Int {
    val (x, y) = parts(a) to parts(b)
    for (i in 0..2) {
        if (x[i] != y[i]) return x[i].compareTo(y[i])   // first difference decides
    }
    return 0
}

/** Gradle's default conflict resolution: for each "group:name", the highest requested version wins. */
fun resolve(requests: List<String>): Map<String, String> =
    requests
        .groupBy({ it.substringBeforeLast(":") }, { it.substringAfterLast(":") })
        .mapValues { (_, versions) -> versions.maxWith(::compareVersions) }
        .toSortedMap()

/** SemVer: may upgrading from -> to break the code that calls this library? */
fun isBreaking(from: String, to: String): Boolean {
    val (old, new) = parts(from) to parts(to)
    if (old[0] == 0) return from != to                  // 0.y.z: "Anything MAY change at any time"
    return new[0] != old[0]
}
