import java.lang.reflect.Method

fun call(target: Any, m: Method, raw: String, isLong: Boolean): Any? {
    val args = listOf(if (isLong) raw.toLong() else raw)     // List<Long> or List<String>?
    return m.invoke(target, *args.toTypedArray())
}
