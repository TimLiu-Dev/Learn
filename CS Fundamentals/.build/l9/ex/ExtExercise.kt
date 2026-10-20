fun String.isPalindrome(): Boolean {
    val cleaned = lowercase().replace(" ", "")
    return cleaned == cleaned.reversed()
}

val Int.isEven: Boolean
    get() = this % 2 == 0

fun List<Int>?.sumOrZero(): Int {
    if (this == null) return 0
    var total = 0
    for (n in this) total += n
    return total
}

fun main() {
    println("Never odd or even".isPalindrome())
    println("Kotlin".isPalindrome())
    println(4.isEven)
    println(7.isEven)
    val none: List<Int>? = null
    println(none.sumOrZero())
    println(listOf(1, 2, 3).sumOrZero())
}
