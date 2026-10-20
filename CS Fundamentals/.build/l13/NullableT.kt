class Box<T>(val item: T)                 // default bound is Any?: T may be nullable
class Strict<T : Any>(val item: T)        // T : Any rules out nullable types

fun <T> describe(x: T): String = "got $x"

fun main() {
    val maybe = Box<String?>(null)        // allowed: T = String?
    println(maybe.item?.length)
    println(describe(null))               // T = Nothing?, still fine
    val sure = Strict("hi")
    println(sure.item.length)             // no ?. needed: item can't be null
}
