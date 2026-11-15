// Splits at every '=' and keeps only the second piece.
fun parseEnv(lines: List<String>): Map<String, String> {
    val env = LinkedHashMap<String, String>()
    for (line in lines) {
        if (line.isBlank() || line.startsWith("#") || '=' !in line) continue
        val parts = line.split("=")
        env[parts[0]] = parts[1]
    }
    return env
}

fun which(cmd: String, env: Map<String, String>, files: Set<String>): String? {
    val path = env["PATH"] ?: return null
    return path.split(':').map { "$it/$cmd" }.firstOrNull { it in files }
}
