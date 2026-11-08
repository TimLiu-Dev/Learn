data class Box(var size: Int)

fun main() {
    val box = Box(1)
    val set = hashSetOf(box)
    box.size = 2                 // changed after it went into the set
    println("${set.contains(box)} ${set.size}")
}
