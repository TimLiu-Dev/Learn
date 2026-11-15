import java.util.concurrent.atomic.AtomicInteger

fun main() {
    val n = AtomicInteger(5)
    val first = n.compareAndSet(5, 6)
    val second = n.compareAndSet(5, 7)
    val old = n.getAndIncrement()
    val new = n.incrementAndGet()
    println("$first $second $old $new")
}
