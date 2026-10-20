import java.lang.reflect.InvocationTargetException

class Calc {
    fun add(a: Int, b: Int) = a + b
    fun divide(a: Int, b: Int) = a / b
}

fun main() {
    val calc = Calc()
    val k = Calc::class.java

    // 1. A typo compiles fine and fails only when this line runs.
    try {
        k.getMethod("ad", Int::class.javaPrimitiveType, Int::class.javaPrimitiveType)
    } catch (e: NoSuchMethodException) {
        println("1. NoSuchMethodException: ${e.message}")
    }

    // 2. Exceptions thrown by the target arrive wrapped.
    try {
        k.getMethod("divide", Int::class.javaPrimitiveType, Int::class.javaPrimitiveType).invoke(calc, 1, 0)
    } catch (e: InvocationTargetException) {
        println("2. ${e.javaClass.simpleName}, cause: ${e.cause}")
    }

    // 3. JDK internals are strongly encapsulated (JEP 403).
    try {
        String::class.java.getDeclaredField("value").isAccessible = true
    } catch (e: RuntimeException) {
        println("3. ${e.javaClass.simpleName}: ${e.message}")
    }

    // 4. A rough speed comparison. Not a benchmark: L24's JMH is the honest tool.
    val add = k.getMethod("add", Int::class.javaPrimitiveType, Int::class.javaPrimitiveType)
    val n = 20_000_000
    repeat(3) { round ->
        var sum = 0L
        var t = System.nanoTime()
        for (i in 0 until n) sum += calc.add(i, 1)
        val direct = (System.nanoTime() - t).toDouble() / n
        t = System.nanoTime()
        for (i in 0 until n) sum += add.invoke(calc, i, 1) as Int
        val reflective = (System.nanoTime() - t).toDouble() / n
        println("4. round $round: direct %.2f ns, reflective %.2f ns per call (sum=$sum)".format(direct, reflective))
    }
}
