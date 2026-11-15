package core

/** A SemVer release number: MAJOR.MINOR.PATCH (no pre-release tags). */
data class Version(val major: Int, val minor: Int, val patch: Int) : Comparable<Version> {

    // Compare numerically, part by part: 1.10.0 > 1.9.0.
    override fun compareTo(other: Version): Int =
        compareValuesBy(this, other, Version::major, Version::minor, Version::patch)

    override fun toString() = "$major.$minor.$patch"

    companion object {
        fun parse(text: String): Version {
            val parts = text.split(".").map { it.toInt() }
            require(parts.size == 3) { "expected MAJOR.MINOR.PATCH, got '$text'" }
            return Version(parts[0], parts[1], parts[2])
        }
    }
}

