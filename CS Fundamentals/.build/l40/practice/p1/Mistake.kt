fun hunkHeader(oldStart: Int, newStart: Int, body: List<String>): String {
    // Counts every line that isn't an addition as an old line, "\ No newline…" included.
    val oldCount = body.count { !it.startsWith("+") }
    val newCount = body.count { !it.startsWith("-") }
    return "@@ -${range(oldStart, oldCount)} +${range(newStart, newCount)} @@"
}

fun range(start: Int, count: Int) = if (count == 1) "$start" else "$start,$count"
