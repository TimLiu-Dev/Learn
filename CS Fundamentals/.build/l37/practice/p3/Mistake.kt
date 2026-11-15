class Holds {
    private val lines = mutableMapOf<String, MutableList<String>>()

    fun place(title: String, member: String): Int {
        val line = lines.getOrPut(title) { mutableListOf() }
        if (member !in line) line.add(member)
        return line.indexOf(member) + 1
    }

    fun next(title: String): String? = lines[title]?.removeFirstOrNull()

    fun cancel(title: String, member: String) {
        lines[title]?.remove(member)
    }

    // Typed as read-only List, but it's the real MutableList: the field is private, the data isn't.
    fun waiting(title: String): List<String> = lines[title] ?: emptyList()
}
