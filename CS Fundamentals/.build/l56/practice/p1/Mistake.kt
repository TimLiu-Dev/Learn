class MiniCask(val log: MutableList<String> = mutableListOf()) {
    private val index = HashMap<String, Int>()

    init {
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
        index.remove(key)                           // forgets it in memory only
    }

    fun compact() {
        val live = index.map { (key, offset) -> key to log[offset] }
        log.clear()
        for ((key, record) in live) {
            log += record
            index[key] = log.lastIndex
        }
    }
}
