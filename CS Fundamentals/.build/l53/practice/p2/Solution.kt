// Lines like `env` prints: KEY=value
fun parseEnv(lines: List<String>): Map<String, String> {
    val env = LinkedHashMap<String, String>()
    for (line in lines) {
        if (line.isBlank() || line.startsWith("#") || '=' !in line) continue
        env[line.substringBefore('=')] = line.substringAfter('=')   // a later line overwrites
    }
    return env
}

// which: the first PATH directory that holds cmd wins
fun which(cmd: String, env: Map<String, String>, files: Set<String>): String? {
    val path = env["PATH"] ?: return null
    return path.split(':').map { "$it/$cmd" }.firstOrNull { it in files }
}
