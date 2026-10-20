fun main() {
    var total = 0L
    for (customer in 1..1000) {
        val earned = customer * 8000L
        total += earned
    }
    println("total points: $total")
}
