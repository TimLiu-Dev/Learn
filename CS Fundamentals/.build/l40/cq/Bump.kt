fun bump(message: String) = when {
    message.startsWith("fix") -> "patch"
    message.startsWith("feat") -> "minor"
    message.contains("!:") -> "major"
    else -> "none"
}

fun main() {
    val log = listOf("feat!: drop the /v1 routes", "fix(api)!: rename title to name", "chore: bump Gradle")
    println(log.map(::bump))
}
