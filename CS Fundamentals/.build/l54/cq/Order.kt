fun log(x: Int): Int {
    print("$x ")
    return x
}

fun main() {
    println(log(1) + log(2) * log(3))
}
