// 5 philosophers, 5 forks. Each needs the fork on the left and the right to eat.
import java.lang.management.ManagementFactory
import kotlin.concurrent.thread

const val N = 5
const val MEALS = 100_000

fun main(args: Array<String>) {
    val ordered = args.firstOrNull() == "ordered"
    val forks = List(N) { Any() }
    val meals = IntArray(N)
    val diners = List(N) { me ->
        val left = me
        val right = (me + 1) % N
        val first = if (ordered) minOf(left, right) else left       // the fix: lower number first
        val second = if (ordered) maxOf(left, right) else right
        thread(isDaemon = true, name = "philosopher-$me") {
            repeat(MEALS) {
                synchronized(forks[first]) {
                    synchronized(forks[second]) {
                        meals[me]++
                    }
                }
            }
        }
    }
    diners.forEach { it.join(2000) }
    val stuck = ManagementFactory.getThreadMXBean().findDeadlockedThreads()
    if (stuck != null) println("DEADLOCK: ${stuck.size} philosophers each hold one fork")
    else println("everyone ate $MEALS meals")
}
