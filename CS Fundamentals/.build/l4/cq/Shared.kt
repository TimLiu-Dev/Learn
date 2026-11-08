class Counter(var count: Int)

fun main() {
    var a = Counter(1)
    val list = mutableListOf(a, a)
    a = Counter(5)
    list[0].count = 9
    println("${a.count} ${list[0].count} ${list[1].count}")
}
