class Counter(var count: Int)

fun makeCounter(start: Int): Counter {
    val fresh = Counter(start)   // object on the heap, reference in this frame
    return fresh                 // the frame dies; the reference is handed back
}

fun main() {
    val kept = makeCounter(10)
    kept.count++
    println(kept.count)
}
