fun main() {
    val lists1 = listOf(listOf(1, 2))
    val lists2 = listOf(listOf(1, 2))
    val arrays1 = listOf(intArrayOf(1, 2))
    val arrays2 = listOf(intArrayOf(1, 2))
    println("${lists1 == lists2} ${arrays1 == arrays2}")
}
