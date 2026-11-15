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
    override fun write(line: String) = TODO("number the line, then pass it on")
}

class Shortened(private val inner: Log, private val max: Int) : Log by inner {
    override fun write(line: String) = TODO("cut the line to max characters, then pass it on")
}

class SkipBlank(private val inner: Log) : Log by inner {
    override fun write(line: String) = TODO("pass on only lines with something in them")
}
