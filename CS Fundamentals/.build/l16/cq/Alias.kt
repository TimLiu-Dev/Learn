fun main() {
    var data: Array<Int?> = arrayOf(1, 2)
    val old = data
    data = data.copyOf(4)          // "grow" to capacity 4
    data[0] = 99
    println("${old[0]} ${old.size} ${data.size}")
}
