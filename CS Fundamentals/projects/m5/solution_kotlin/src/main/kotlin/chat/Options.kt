package chat

import java.nio.file.Path
import kotlin.time.Duration
import kotlin.time.Duration.Companion.seconds

const val USAGE =
    "usage: chat --tcp-port P --http-port Q --users FILE [--token-ttl SECONDS] [--idle-timeout SECONDS] [--max-queue N]"

/** Bad command line or unreadable input file: main prints "error: <message>" and exits with 2. */
class UsageError(message: String) : Exception(message)

data class Options(
    val tcpPort: Int,
    val httpPort: Int,
    val usersFile: Path,
    val tokenTtl: Duration = 3600.seconds,
    val idleTimeout: Duration = 300.seconds,
    val maxQueue: Int = 1024,
)

private val FLAGS = setOf("--tcp-port", "--http-port", "--users", "--token-ttl", "--idle-timeout", "--max-queue")

fun parseOptions(args: Array<String>): Options {
    val values = mutableMapOf<String, String>()
    var i = 0
    while (i < args.size) {
        val flag = args[i]
        if (flag !in FLAGS) throw UsageError("unknown option '$flag'")
        values[flag] = args.getOrNull(i + 1) ?: throw UsageError("$flag needs a value")
        i += 2
    }
    fun int(flag: String, range: IntRange, default: Int? = null): Int {
        val raw = values[flag] ?: return default ?: throw UsageError("$flag is required")
        val n = raw.toIntOrNull()
        if (n == null || n !in range) throw UsageError("$flag must be a number in $range, got '$raw'")
        return n
    }
    return Options(
        tcpPort = int("--tcp-port", 0..65535),
        httpPort = int("--http-port", 0..65535),
        usersFile = Path.of(values["--users"] ?: throw UsageError("--users is required")),
        tokenTtl = int("--token-ttl", 1..86_400, 3600).seconds,
        idleTimeout = int("--idle-timeout", 1..86_400, 300).seconds,
        maxQueue = int("--max-queue", 1..1_000_000, 1024),
    )
}
