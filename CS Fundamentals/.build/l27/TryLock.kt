// Fix 2: never wait forever. Try both locks with a timeout; on failure, let go and retry later.
import java.util.concurrent.ThreadLocalRandom
import java.util.concurrent.TimeUnit
import java.util.concurrent.atomic.AtomicInteger
import java.util.concurrent.locks.ReentrantLock
import kotlin.concurrent.thread

class LockedAccount {
    val lock = ReentrantLock()
    var balance = 1_000L
}

val retries = AtomicInteger()

fun transfer(from: LockedAccount, to: LockedAccount, amount: Long) {
    while (true) {
        if (from.lock.tryLock(1, TimeUnit.MILLISECONDS)) {
            try {
                if (to.lock.tryLock(1, TimeUnit.MILLISECONDS)) {
                    try {
                        from.balance -= amount
                        to.balance += amount
                        return                                   // success
                    } finally {
                        to.lock.unlock()
                    }
                }
            } finally {
                from.lock.unlock()                               // give up the first lock too
            }
        }
        retries.incrementAndGet()
        Thread.sleep(0, ThreadLocalRandom.current().nextInt(1_000, 100_000)) // random back-off
    }
}

fun main() {
    val alice = LockedAccount()
    val bob = LockedAccount()
    val t1 = thread { repeat(10_000) { transfer(alice, bob, 1) } }
    val t2 = thread { repeat(10_000) { transfer(bob, alice, 1) } }
    t1.join(); t2.join()
    println("total=${alice.balance + bob.balance} after 20,000 transfers, ${retries.get()} retries")
}
