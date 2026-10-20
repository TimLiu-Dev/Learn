// The real ones, from the standard library:
//   inline fun <T> T.apply(block: T.() -> Unit): T
//   inline fun <T, R> T.let(block: (T) -> R): R
inline fun <T> T.myApply(block: T.() -> Unit): T {
    block()                     // call the lambda with `this` as its receiver
    return this
}

inline fun <T, R> T.myLet(block: (T) -> R): R = block(this)

// A tiny DSL: inside the lambda the receiver is an HtmlList, so item(...) needs no prefix
class HtmlList {
    private val items = mutableListOf<String>()
    fun item(text: String) { items += "<li>$text</li>" }
    fun render() = "<ul>" + items.joinToString("") + "</ul>"
}

fun htmlList(build: HtmlList.() -> Unit): String = HtmlList().apply(build).render()

fun main() {
    val sb = StringBuilder().myApply { append("built "); append("with myApply") }
    println(sb)
    println("kotlin".myLet { it.length })

    val greet: String.() -> String = { "hello, $this" }      // a function type WITH a receiver
    println("ada".greet())                                     // call it like an extension...
    println(greet("bob"))                                      // ...or pass the receiver first

    println(buildString { append("abc"); append(123) })
    println(htmlList { item("eggs"); item("milk") })
}
