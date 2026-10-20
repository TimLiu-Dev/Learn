// Every comparison goes through less(), so we can count them (Sedgewick's trick).
object Count { var compares = 0L }

fun less(a: Int, b: Int): Boolean {
    Count.compares++
    return a < b
}

fun IntArray.swap(i: Int, j: Int) {
    val t = this[i]; this[i] = this[j]; this[j] = t
}
