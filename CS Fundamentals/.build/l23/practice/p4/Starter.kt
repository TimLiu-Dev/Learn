// "at CLASS.METHOD(FILE:LINE)"
private val FRAME = Regex("""at (.+)\.([^.]+)\(([^:]+):(\d+)\)""")

fun retrace(mapping: String, frame: String): String {
    val (obfClass, obfMethod, file, line) = FRAME.matchEntire(frame)?.destructured ?: return frame
    TODO("find the class line for $obfClass, then the method line for $obfMethod that owns line $line")
}
