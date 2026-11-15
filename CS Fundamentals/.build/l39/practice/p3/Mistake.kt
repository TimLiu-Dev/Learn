interface Log {
    fun write(line: String)
    fun lines(): List<String>
}

class MemoryLog : Log {
    private val stored = mutableListOf<String>()
    override fun write(line: String) { stored += line }
    override fun lines(): List<String> = stored.toList()
}

class Numbered(private val inner: Log) : Log by inner {
    private var count = 0
    override fun write(line: String) { write("${++count}: $line") }      // calls itself, not inner
}

class Shortened(private val inner: Log, private val max: Int) : Log by inner {
    override fun write(line: String) = inner.write(line.take(max))
}

class SkipBlank(private val inner: Log) : Log by inner {
    override fun write(line: String) {
        if (line.isNotBlank()) inner.write(line)
    }
}
