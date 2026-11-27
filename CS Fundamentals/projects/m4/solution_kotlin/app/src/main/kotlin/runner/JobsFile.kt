package runner

/** Parses and validates a jobs file. Every problem is an [InputError] naming the line. */
object JobsFile {
    private val ID = Regex("[A-Za-z0-9_-]+")
    private val KEYS = setOf("id", "duration", "fail", "deps")

    fun parse(text: String): List<Job> {
        val jobs = mutableListOf<Job>()
        val lineOf = mutableMapOf<String, Int>()
        text.lines().forEachIndexed { index, raw ->
            val n = index + 1
            val line = raw.trim()
            if (line.isEmpty() || line.startsWith("#")) return@forEachIndexed
            val fields = mutableMapOf<String, String>()
            for (token in line.split(Regex("\\s+"))) {
                val eq = token.indexOf('=')
                if (eq < 0) throw InputError("line $n: expected key=value, got '$token'")
                val key = token.substring(0, eq)
                if (key !in KEYS) throw InputError("line $n: unknown key '$key'")
                if (fields.put(key, token.substring(eq + 1)) != null) throw InputError("line $n: '$key' given twice")
            }
            val id = fields["id"] ?: throw InputError("line $n: missing id")
            if (!ID.matches(id)) throw InputError("line $n: bad id '$id'")
            lineOf[id]?.let { throw InputError("line $n: duplicate id '$id' (first on line $it)") }
            lineOf[id] = n
            val duration = number(fields["duration"] ?: throw InputError("line $n: missing duration"), "duration", n)
            val fail = number(fields["fail"] ?: "0", "fail", n)
            val deps = fields["deps"].orEmpty().split(',').filter { it.isNotEmpty() }
            jobs += Job(id, duration, fail.toInt(), deps)
        }
        for (job in jobs) for (dep in job.deps)
            if (dep !in lineOf) throw InputError("line ${lineOf[job.id]}: unknown dependency '$dep' in job '${job.id}'")
        return jobs
    }

    private fun number(value: String, key: String, line: Int): Long {
        val v = value.toLongOrNull()
        if (v == null || v < 0) throw InputError("line $line: $key must be a whole number ≥ 0, got '$value'")
        return v
    }
}
