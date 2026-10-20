inline fun <T> T.myAlso(block: (T) -> Unit): T {
    block(this)
    return this
}

inline fun <T> T.myTakeIf(predicate: (T) -> Boolean): T? = if (predicate(this)) this else null

inline fun <T, R> T.myRun(block: T.() -> R): R = block()

fun main() {
    val n = 41.myAlso { println("saw $it") } + 1
    println(n)
    println("abc".myTakeIf { it.length == 3 })
    println("abcd".myTakeIf { it.length == 3 })
    println("kotlin".myRun { uppercase() + length })
}
