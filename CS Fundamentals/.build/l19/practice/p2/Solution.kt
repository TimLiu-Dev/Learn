// Linear probing for Ints, kept at most half full so probe runs stay short.
class IntSet {
    private var slots = arrayOfNulls<Int>(8)
    var size = 0
        private set
    val capacity: Int get() = slots.size

    fun contains(x: Int): Boolean = slots[find(slots, x)] == x

    fun add(x: Int): Boolean {
        val i = find(slots, x)
        if (slots[i] == x) return false
        slots[i] = x
        size++
        if (size > capacity / 2) resize()
        return true
    }

    private fun resize() {
        val old = slots
        slots = arrayOfNulls(old.size * 2)
        for (x in old) {
            if (x != null) slots[find(slots, x)] = x   // its home depends on the new size
        }
    }

    // The slot holding x, or the first empty slot on its probe path.
    private fun find(table: Array<Int?>, x: Int): Int {
        var i = x.mod(table.size)
        while (table[i] != null && table[i] != x) i = (i + 1) % table.size
        return i
    }
}
