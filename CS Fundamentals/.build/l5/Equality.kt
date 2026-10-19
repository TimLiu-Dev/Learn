class Box(val value: Int)            // no equals() of its own

fun main() {
    val b1 = Box(1)
    val b2 = Box(1)
    val b3 = b1
    println("${b1 == b2} ${b1 === b2} ${b1 === b3}")   // Box: equality = identity by default

    val s1 = "hi"
    val s2 = StringBuilder("h").append("i").toString()
    println("${s1 == s2} ${s1 === s2}")               // String overrides equals()

    val l1 = listOf(1, 2)
    val l2 = listOf(1, 2)
    println("${l1 == l2} ${l1 === l2}")               // List overrides equals()

    val a1 = arrayOf(1, 2)
    val a2 = arrayOf(1, 2)
    println("${a1 == a2} ${a1.contentEquals(a2)}")    // arrays do NOT override equals()
}
