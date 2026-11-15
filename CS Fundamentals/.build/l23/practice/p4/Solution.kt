// "at CLASS.METHOD(FILE:LINE)"
private val FRAME = Regex("""at (.+)\.([^.]+)\(([^:]+):(\d+)\)""")
private val RANGE = Regex("""^(\d+):(\d+):""")

fun retrace(mapping: String, frame: String): String {
    val (obfClass, obfMethod, file, line) = FRAME.matchEntire(frame)?.destructured ?: return frame
    var inClass = false
    var originalClass: String? = null
    var originalMethod = obfMethod
    for (row in mapping.lines().filter { it.isNotBlank() }) {
        val (left, right) = row.trim().split(" -> ")
        if (!row.startsWith(" ")) {                               // class line
            inClass = right.removeSuffix(":") == obfClass
            if (inClass) originalClass = left
        } else if (inClass && right == obfMethod && '(' in left) { // a method, not a field
            val range = RANGE.find(left)?.destructured
            if (range == null || line.toInt() in range.component1().toInt()..range.component2().toInt()) {
                originalMethod = left.substringBefore('(').substringAfterLast(' ')
            }
        }
    }
    return if (originalClass == null) frame else "at $originalClass.$originalMethod($file:$line)"
}
