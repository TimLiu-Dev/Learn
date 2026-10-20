// PRODUCER: List is declared List<out E>, so List<Int> already IS a List<Number>
fun sum(nums: List<Number>): Double {
    var total = 0.0
    for (n in nums) total += n.toDouble()
    return total
}

// CONSUMER: MutableList is invariant, so say "in" at the use site
fun addSquares(out: MutableList<in Int>, n: Int) {
    for (i in 1..n) out.add(i * i)
}

// both at once: like Collections.copy
fun <T> copyInto(src: List<T>, dst: MutableList<in T>) {
    for (item in src) dst.add(item)
}

// unknown element type: star projection, read as Any?
fun countNonNull(items: List<*>): Int {
    var c = 0
    for (o in items) if (o != null) c++
    return c
}

// use-site projection on an invariant class: Array<out Any> is a read-only view
fun describeAll(items: Array<out Any>): String = items.joinToString("|")

fun main() {
    val ints = listOf(1, 2, 3)
    val doubles = listOf(0.5, 0.25)
    println("${sum(ints)} ${sum(doubles)}")

    val numbers = mutableListOf<Number>()
    val objects = mutableListOf<Any>()
    addSquares(numbers, 3)
    addSquares(objects, 2)
    println("$numbers $objects")

    copyInto(doubles, numbers)                    // T = Double, dst accepts it
    println(numbers)
    println("${countNonNull(listOf("a", "b"))} ${countNonNull(ints)}")
    println(describeAll(arrayOf("x", "y")))       // Array<String> fits Array<out Any>
}
