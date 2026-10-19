fun c(): Int {
    throw IllegalStateException("boom")
}

fun b(): Int = c() * 2

fun a(): Int = b() + 1

fun main() {
    println(a())
}
