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
        if (key !in index) return
        log += "del $key"
        index.remove(key)
    }

    fun compact() {
        val live = index.values.sorted().map { log[it] }
        log.clear()
        log.addAll(live)                            // the index still holds the old offsets
    }
}
