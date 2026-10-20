fun main() {
    val lines = mutableListOf<String>()
    for (i in 1..1000) lines.add(if (i == 641) "64l" else i.toString())   // one typo hiding in 1000 lines

    var sum = 0L
    for ((index, line) in lines.withIndex()) {
        sum += line.toInt()
    }
    println("sum: $sum")
}
