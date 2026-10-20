fun main() {
    var total = 0
    for (customer in 1..1000) {
        val earned = customer * 8000
        total += earned
    }
    println("total points: $total")
}
