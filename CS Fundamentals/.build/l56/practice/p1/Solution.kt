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
        log += "put $key $value"
        index[key] = log.lastIndex
    }

    fun get(key: String): String? {
        val offset = index[key] ?: return null
        return log[offset].split(" ", limit = 3)[2]
    }

    fun delete(key: String) {
        if (key !in index) return
        log += "del $key"                           // the tombstone: replay must see the delete too
        index.remove(key)
    }

    fun compact() {
        val live = index.map { (key, offset) -> key to log[offset] }
        log.clear()
        for ((key, record) in live) {
            log += record
            index[key] = log.lastIndex              // every offset changed
        }
    }
}
