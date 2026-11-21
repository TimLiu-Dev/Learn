fun decide(votes: Map<String, Boolean>) = if (votes.values.all { it }) "COMMIT" else "ABORT"

fun main() {
    // participants: orders-db and stock-db
    val votes = mutableMapOf<String, Boolean>()
    val early = decide(votes)
    votes["orders-db"] = true
    val half = decide(votes)
    votes["stock-db"] = false
    println("$early $half ${decide(votes)}")
}
