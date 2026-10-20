// Kotlin side: takes a Kotlin function type
fun applyTwice(f: (Int) -> Int, x: Int): Int = f(f(x))

fun main() {
    // Kotlin calling a Java method that wants java.util.function.Function
    println(JavaTools.applyTwiceJ({ it * 3 }, 2))   // lambda converted to Function
    val inc = java.util.function.Function<Int, Int> { it + 1 }
    println(inc.apply(41))                            // a Java object: call .apply
}
