// Lines like `env` prints: KEY=value
fun parseEnv(lines: List<String>): Map<String, String> =
    TODO("skip blanks, comments and lines without '='; split at the first '='")

// which: the first PATH directory that holds cmd wins
fun which(cmd: String, env: Map<String, String>, files: Set<String>): String? =
    TODO("split PATH on ':' and try \"\$dir/\$cmd\" in order")
