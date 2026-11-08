class Box {
    var n = 1
    val eager = n * 10
    val lazy by lazy { n * 10 }
    val getter get() = n * 10
}

fun main() {
    val b = Box()
    b.n = 2
    val first = b.lazy
    b.n = 3
    println("${b.eager} $first ${b.lazy} ${b.getter}")
}
