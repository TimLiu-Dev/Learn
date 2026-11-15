/**
 * Holds on books, first come, first served.
 * No operation throws: asking about a title nobody wants is just an empty line.
 */
class Holds {
    private val lines = mutableMapOf<String, ArrayDeque<String>>()   // title -> members, in order

    /** Puts [member] in line for [title] (once) and returns their 1-based place. */
    fun place(title: String, member: String): Int {
        val line = lines.getOrPut(title) { ArrayDeque() }
        if (member !in line) line.addLast(member)
        return line.indexOf(member) + 1
    }

    /** Removes and returns the first member waiting for [title], or null if nobody is. */
    fun next(title: String): String? = lines[title]?.removeFirstOrNull()

    fun cancel(title: String, member: String) {
        lines[title]?.remove(member)
    }

    /** Who's waiting for [title], in order. A copy: changing it doesn't change the queue. */
    fun waiting(title: String): List<String> = lines[title]?.toList() ?: emptyList()
}
