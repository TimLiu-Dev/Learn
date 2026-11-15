fun countdown(n: Int): String {
    if (n == 0) return ""
    return "$n " + countdown(n - 1) + "$n "
}

fun main() {
    println(countdown(3).trim())
}
