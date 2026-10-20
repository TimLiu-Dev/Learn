data class Score(val student: String, val subject: String, val points: Int)

val scores = listOf(
    Score("Ada", "math", 92), Score("Ada", "art", 71), Score("Lin", "math", 48),
    Score("Lin", "art", 88), Score("Sam", "math", 75), Score("Sam", "art", 45),
    Score("Kim", "math", 81),
)

fun main() {
    // 1. who failed anything (below 50)? distinct, alphabetical
    println(scores.filter { it.points < 50 }.map { it.student }.distinct().sorted())
    // 2. average per subject
    println(scores.groupBy { it.subject }.mapValues { (_, s) -> s.map { it.points }.average() })
    // 3. student with the highest total
    val totals = scores.groupBy { it.student }.mapValues { (_, s) -> s.sumOf { it.points } }
    println(totals.maxByOrNull { it.value }?.key)
    // 4. math ranking, best first, as "name points"
    println(scores.filter { it.subject == "math" }.sortedByDescending { it.points }
        .joinToString { "${it.student} ${it.points}" })
}
