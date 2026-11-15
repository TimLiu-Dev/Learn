/** Compares "MAJOR.MINOR.PATCH" numerically: negative if a < b, 0 if equal, positive if a > b. */
fun compareVersions(a: String, b: String): Int =
    TODO("split both on '.', then compare the numbers part by part")

/** Gradle's default conflict resolution: for each "group:name", the highest requested version wins. */
fun resolve(requests: List<String>): Map<String, String> =
    TODO("group the coordinates by module, then keep the highest version of each")

/** SemVer: may upgrading from -> to break the code that calls this library? */
fun isBreaking(from: String, to: String): Boolean =
    TODO("compare the MAJOR parts, and remember what SemVer says about 0.y.z")
