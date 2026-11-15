// Open addressing: one key per slot. On a collision, try the next slot.
class ProbingMap(capacity: Int) {
    private val keys = arrayOfNulls<String>(capacity)
    private val values = IntArray(capacity)
    var size = 0
        private set

    fun slots(): List<String?> = keys.toList()

    // The slot holding key, or the first empty slot on its probe path. -1 if the table is full.
    private fun find(key: String): Int {
        var i = key.hashCode().mod(keys.size)     // mod, not %: hash codes can be negative
        repeat(keys.size) {                       // at most one visit per slot, so a full table can't loop forever
            if (keys[i] == null || keys[i] == key) return i
            i = (i + 1) % keys.size               // i is never negative here, so % is safe
        }
        return -1
    }

    fun put(key: String, value: Int) {
        val i = find(key)
        check(i >= 0) { "table is full" }
        if (keys[i] == null) {
            keys[i] = key
            size++
        }
        values[i] = value
    }

    fun get(key: String): Int? {
        val i = find(key)
        return if (i >= 0 && keys[i] == key) values[i] else null
    }
}
