class Counter {
    var n = 0
}

fun work(c: Counter, name: String) {
    var local = 0                 // lives in this thread's own stack frame
    repeat(3) {
        local++
        c.n++
    }
    println("$name: local=$local, shared=${c.n}")
}

fun main() {
    val c = Counter()             // one object, on the heap both threads share
    val t = Thread { work(c, "t") }
    t.start()
    t.join()                      // wait, so the order is fixed
    work(c, "main")
}
