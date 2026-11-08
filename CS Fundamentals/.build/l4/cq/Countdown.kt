fun countdown(n: Int) {
    if (n == 0) return
    val label = "n=$n"
    countdown(n - 1)
    print("$label ")
}

fun main() {
    countdown(3)
    println()
}
