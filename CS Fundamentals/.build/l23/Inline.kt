inline fun twice(block: () -> Unit) {
    block()
    block()
}

fun twiceNoInline(block: () -> Unit) {
    block()
    block()
}

fun main() {
    var count = 0
    twice { count++ }
    twiceNoInline { println("hi") }
    println(count)
}
