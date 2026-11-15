data class Compile(
    val ms: Int, val id: Int, val osr: Boolean, val tier: Int?,
    val method: String, val bytes: Int, val retired: Boolean,
)

fun parse(line: String): Compile {
    val tokens = line.trim().split(Regex("\\s+"))
    val methodAt = tokens.indexOfFirst { "::" in it }
    val middle = tokens.subList(2, methodAt)
    val size = tokens[tokens.indexOfFirst { it.startsWith("(") }]
    return Compile(tokens[0].toInt(), tokens[1].toInt(), middle.any { '%' in it },
        middle.lastOrNull()?.toIntOrNull(), tokens[methodAt], size.removePrefix("(").toInt(),
        "made not entrant" in line)
}

// Forgets OSR: the first tier-4 line is the loop compiled mid-run, not the whole method.
fun c2Landed(lines: List<String>, method: String): Int? =
    lines.map(::parse).firstOrNull { it.method == method && it.tier == 4 && !it.retired }?.ms
