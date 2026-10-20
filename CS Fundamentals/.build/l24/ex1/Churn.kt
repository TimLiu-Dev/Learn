// Allocates ~4 GB of short-lived objects while keeping a small window alive.
data class Order(val id: Long, val customer: String, val items: IntArray)

fun main() {
    val recent = arrayOfNulls<Order>(400_000)     // the "live" window: 400k orders
    var checksum = 0L
    val start = System.nanoTime()
    for (i in 0L until 20_000_000L) {
        val o = Order(i, "customer-${i % 1000}", IntArray(16))
        recent[(i % recent.size).toInt()] = o    // older orders become garbage
        checksum += o.items.size
    }
    val ms = (System.nanoTime() - start) / 1_000_000
    println("checksum $checksum in $ms ms")
    System.gc()                                  // only a request (see below)
}
