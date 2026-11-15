// Open addressing: one key per slot. On a collision, try the next slot.
class ProbingMap(capacity: Int) {
    private val keys = arrayOfNulls<String>(capacity)
    private val values = IntArray(capacity)
    var size = 0
        private set

    fun slots(): List<String?> = keys.toList()

    fun put(key: String, value: Int) {
        TODO("probe from the home slot until you find the key or an empty slot")
    }

    fun get(key: String): Int? = TODO("probe the same way; an empty slot means absent")
}
