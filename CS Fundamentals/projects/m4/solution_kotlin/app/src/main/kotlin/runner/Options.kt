package runner

/** Thrown for anything the user typed wrong: bad flags, a bad jobs file, a dependency cycle. Exit code 2. */
class InputError(message: String) : Exception(message)

data class Options(
    val file: String,
    val workers: Int,
    val retries: Int = 0,
    val timeoutMs: Long = 0,     // 0 = no timeout
    val queue: Int = 16,
    val graceMs: Long = 3000,
) {
    companion object {
        const val USAGE =
            "usage: runner <jobs-file> --workers N [--retries R] [--timeout-ms T] [--queue Q] [--grace-ms G]"

        fun parse(args: Array<String>): Options {
            var file: String? = null
            val flags = mutableMapOf<String, Long>()
            var i = 0
            while (i < args.size) {
                val arg = args[i]
                if (arg.startsWith("--")) {
                    val name = arg.removePrefix("--")
                    if (name !in setOf("workers", "retries", "timeout-ms", "queue", "grace-ms"))
                        throw InputError("unknown option $arg\n$USAGE")
                    val value = args.getOrNull(i + 1) ?: throw InputError("$arg needs a value\n$USAGE")
                    flags[name] = value.toLongOrNull() ?: throw InputError("$arg must be a number, got '$value'")
                    i += 2
                } else {
                    if (file != null) throw InputError("only one jobs file, got '$file' and '$arg'\n$USAGE")
                    file = arg
                    i++
                }
            }
            if (file == null) throw InputError("missing jobs file\n$USAGE")
            val workers = flags["workers"] ?: throw InputError("missing --workers\n$USAGE")
            val opts = Options(
                file = file,
                workers = workers.toInt(),
                retries = (flags["retries"] ?: 0).toInt(),
                timeoutMs = flags["timeout-ms"] ?: 0,
                queue = (flags["queue"] ?: 16).toInt(),
                graceMs = flags["grace-ms"] ?: 3000,
            )
            if (opts.workers < 1) throw InputError("--workers must be at least 1, got ${opts.workers}")
            if (opts.queue < 1) throw InputError("--queue must be at least 1, got ${opts.queue}")
            if (opts.retries < 0) throw InputError("--retries must be 0 or more, got ${opts.retries}")
            if (opts.timeoutMs < 0) throw InputError("--timeout-ms must be 0 or more, got ${opts.timeoutMs}")
            if (opts.graceMs < 0) throw InputError("--grace-ms must be 0 or more, got ${opts.graceMs}")
            return opts
        }
    }
}
