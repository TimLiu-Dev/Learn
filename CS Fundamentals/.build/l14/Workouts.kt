data class Workout(val day: String, val sport: String, val minutes: Int, val kcal: Int)

val log = listOf(
    Workout("Mon", "run", 32, 350), Workout("Mon", "yoga", 20, 90),
    Workout("Tue", "bike", 45, 420), Workout("Wed", "run", 25, 280),
    Workout("Thu", "swim", 40, 380), Workout("Fri", "run", 50, 540),
    Workout("Sat", "bike", 90, 810), Workout("Sun", "yoga", 30, 130),
)

fun main() {
    // 1. filter + map
    println(log.filter { it.sport == "run" && it.minutes >= 30 }.map { it.day })
    // 2. sum of a property
    println(log.sumOf { it.minutes })
    // 3. group, then total each group
    println(log.groupBy { it.sport }.mapValues { (_, ws) -> ws.sumOf { it.minutes } })
    // 4. count per group
    println(log.groupingBy { it.sport }.eachCount())
    // 5. the max by some property: null if the list is empty
    println(log.maxByOrNull { it.kcal }?.let { "${it.day} ${it.sport}" })
    // 6. sort by two keys, keep the top 3
    println(log.sortedWith(compareByDescending<Workout> { it.minutes }.thenBy { it.day })
        .take(3).map { "${it.sport}:${it.minutes}" })
    // 7. split in two
    val (long, short) = log.partition { it.minutes >= 40 }
    println("${long.size} long, ${short.size} short")
    // 8. yes/no questions, and joining
    println("${log.any { it.sport == "swim" }} ${log.all { it.minutes >= 20 }} ${log.none { it.kcal > 1000 }}")
    println(log.map { it.sport }.distinct().sorted().joinToString(", ", "[", "]"))
    // 9. first match, or null
    println(log.firstOrNull { it.sport == "rowing" })
}
