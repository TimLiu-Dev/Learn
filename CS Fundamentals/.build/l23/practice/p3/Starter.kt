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
    TODO("read the fields; the tokens before methodAt are time, id, flags and maybe a tier")
}

fun c2Landed(lines: List<String>, method: String): Int? =
    TODO("time of the first tier-4, non-OSR, non-retired compile of method")
