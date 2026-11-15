// Part 1's store, with the file modelled as a list of records: "put key value" or "del key".
// Keys contain no spaces. A key's offset is the record's position in the list.
class MiniCask(val log: MutableList<String> = mutableListOf()) {
    private val index = HashMap<String, Int>()      // key -> offset of its newest record

    init {                                          // reopening: replay every record, oldest first
        for ((offset, record) in log.withIndex()) {
            val parts = record.split(" ", limit = 3)
            if (parts[0] == "put") index[parts[1]] = offset else index.remove(parts[1])
        }
    }

    val liveKeys: Int get() = index.size

    fun put(key: String, value: String) {
        TODO("append a record, then point the index at it")
    }

    fun get(key: String): String? {
        TODO("look up the offset, then read the value out of that record")
    }

    fun delete(key: String) {
        TODO("what has to be in the log so that a reopen doesn't bring the value back?")
    }

    fun compact() {
        TODO("keep only the newest record of each live key, in the same list")
    }
}
