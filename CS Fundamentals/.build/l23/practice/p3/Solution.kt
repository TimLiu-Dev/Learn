data class Compile(
    val ms: Int,          // milliseconds since the JVM started
    val id: Int,          // compile ID
    val osr: Boolean,     // '%' : on-stack replacement
    val tier: Int?,       // 0–4, or null when tiered compilation is off
    val method: String,   // e.g. "Hot::work"
    val bytes: Int,       // bytecode size
    val retired: Boolean, // "made not entrant"
)

// e.g. "10   10 %     4       Hot::work @ 4 (29 bytes)"
fun parse(line: String): Compile {
    val tokens = line.trim().split(Regex("\\s+"))
    val methodAt = tokens.indexOfFirst { "::" in it }
    val middle = tokens.subList(2, methodAt)              // flag characters, then maybe a tier
    val size = tokens[tokens.indexOfFirst { it.startsWith("(") }]
    return Compile(
        ms = tokens[0].toInt(),
        id = tokens[1].toInt(),
        osr = middle.any { '%' in it },
        tier = middle.lastOrNull()?.toIntOrNull(),
        method = tokens[methodAt],
        bytes = size.removePrefix("(").toInt(),
        retired = "made not entrant" in line,
    )
}

fun c2Landed(lines: List<String>, method: String): Int? =
    lines.map(::parse)
        .firstOrNull { it.method == method && it.tier == 4 && !it.osr && !it.retired }
        ?.ms
