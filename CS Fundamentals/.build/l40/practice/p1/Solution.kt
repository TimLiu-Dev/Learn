/** The header Git prints above a hunk, such as "@@ -16,10 +16,10 @@". */
fun hunkHeader(oldStart: Int, newStart: Int, body: List<String>): String {
    // Context lines exist in both files; "\ No newline…" lines start with '\' and match neither test.
    val oldCount = body.count { it.startsWith(" ") || it.startsWith("-") }
    val newCount = body.count { it.startsWith(" ") || it.startsWith("+") }
    return "@@ -${range(oldStart, oldCount)} +${range(newStart, newCount)} @@"
}

// Git leaves out ",1": a one-line range is just its start.
fun range(start: Int, count: Int) = if (count == 1) "$start" else "$start,$count"
