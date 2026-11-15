private val FRAME = Regex("""at (.+)\.([^.]+)\(([^:]+):(\d+)\)""")

// Ignores line ranges: the first method named "a" always wins.
fun retrace(mapping: String, frame: String): String {
    val (obfClass, obfMethod, file, line) = FRAME.matchEntire(frame)?.destructured ?: return frame
    var inClass = false
    var originalClass: String? = null
    var originalMethod: String? = null
    for (row in mapping.lines().filter { it.isNotBlank() }) {
        val (left, right) = row.trim().split(" -> ")
        if (!row.startsWith(" ")) {
            inClass = right.removeSuffix(":") == obfClass
            if (inClass) originalClass = left
        } else if (inClass && right == obfMethod && '(' in left && originalMethod == null) {
            originalMethod = left.substringBefore('(').substringAfterLast(' ')
        }
    }
    return if (originalClass == null) frame else "at $originalClass.${originalMethod ?: obfMethod}($file:$line)"
}
