// SHALLOW: the storage is public, and every caller must repeat the ritual.
class HoldQueue {
    val holds = mutableMapOf<String, MutableList<String>>()   // title -> members, first come first served
}

fun placeHold(q: HoldQueue, title: String, member: String): Int {
    val line = q.holds.getOrPut(title) { mutableListOf() }
    if (member !in line) line.add(member)
    return line.indexOf(member) + 1
}

fun nextInLine(q: HoldQueue, title: String): String? {
    val line = q.holds[title] ?: return null
    val first = line.removeAt(0)                     // throws if the line is empty
    if (line.isEmpty()) q.holds.remove(title)
    return first
}

// DEEP: same jobs, with the storage and the rules inside.
class Holds {
    fun place(title: String, member: String): Int = TODO("the placeHold ritual, inside")
    fun next(title: String): String? = TODO("null when nobody is waiting")
    fun cancel(title: String, member: String): Unit = TODO("never throws")
    fun waiting(title: String): List<String> = TODO("a copy, not the real list")
}
