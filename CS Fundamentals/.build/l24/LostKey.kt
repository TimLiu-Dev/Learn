// A key mutated after insertion can never be found again, so it can never be removed.
data class Cell(var row: Int)                     // equals/hashCode use a MUTABLE field

fun main() {
    val active = HashSet<Cell>()
    for (i in 0 until 3) {
        val c = Cell(i)
        active.add(c)
        c.row += 100                              // mutate the key while it's inside
        println("removed? ${active.remove(c)}, size ${active.size}")
    }
}
