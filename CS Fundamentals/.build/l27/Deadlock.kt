// Two transfers lock the same two accounts in opposite order. A watchdog reports the deadlock.
import java.lang.management.ManagementFactory
import kotlin.concurrent.thread

class Account(val name: String) {
    var balance = 100L
}

fun transfer(from: Account, to: Account, amount: Long) {
    synchronized(from) {
        Thread.sleep(50)                                // widen the window between the two locks
        synchronized(to) {
            from.balance -= amount
            to.balance += amount
        }
    }
}

fun main() {
    val alice = Account("alice")
    val bob = Account("bob")
    // isDaemon = true: stuck daemons can't stop the JVM exiting
    val t1 = thread(isDaemon = true, name = "t1 alice->bob") { transfer(alice, bob, 10) }
    val t2 = thread(isDaemon = true, name = "t2 bob->alice") { transfer(bob, alice, 20) }

    t1.join(2000)                                       // watchdog: give them 2 s
    t2.join(100)
    val mx = ManagementFactory.getThreadMXBean()
    val ids = mx.findDeadlockedThreads()
    if (ids == null) {
        println("finished: alice=${alice.balance} bob=${bob.balance}")
        return
    }
    println("DEADLOCK between ${ids.size} threads:")
    for (info in mx.getThreadInfo(ids)) {
        println("  ${info.threadName} is ${info.threadState}, waiting for ${info.lockName}" +
                " held by ${info.lockOwnerName}")
    }
}
