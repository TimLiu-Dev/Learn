fun main() {
    val a: Int? = 127
    val b: Int? = 127
    val c: Int? = 128
    val d: Int? = 128
    println(a === b)       // identity of boxes: don't rely on it
    println(c === d)
    println(c == d)        // Kotlin == compares values: always what you want
}
