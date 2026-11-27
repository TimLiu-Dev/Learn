// Stands in for a long-running server: it keeps working while you attach a debugger.
fun main() {
    var total = 0L
    for (tick in 1..30) {                                // stops by itself after ~30 s
        total += tick * 10L
        println("tick $tick, total $total")
        Thread.sleep(1000)
    }
}
