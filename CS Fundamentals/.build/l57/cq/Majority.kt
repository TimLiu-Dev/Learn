fun wins(votes: Int, clusterSize: Int) = votes > clusterSize / 2

fun main() {
    val needed = listOf(3, 4, 5).map { n -> "$n:" + (1..n).first { wins(it, n) } }
    println(needed.joinToString(" "))
}
