fun main() {
    val after: Map<String, String> = mapOf("user:1" to "n1", "user:2" to "n2")
    val changed = listOf("user:1", "user:2")
    println(changed.groupingBy { after[it] }.eachCount().toSortedMap())   // after[it] is String?
}
