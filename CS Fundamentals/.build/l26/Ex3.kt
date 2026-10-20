import java.util.concurrent.CountDownLatch
import java.util.concurrent.atomic.AtomicInteger
import kotlin.concurrent.thread

interface Account { fun withdraw(amount: Int): Boolean; val balance: Int }

class SyncAccount(start: Int) : Account {
    override var balance = start
        @Synchronized get
        private set
    @Synchronized override fun withdraw(amount: Int): Boolean {   // check AND act under one lock
        if (balance < amount) return false
        Thread.sleep(1)
        balance -= amount
        return true
    }
}

class AtomicButWrong(start: Int) : Account {
    private val bal = AtomicInteger(start)
    override val balance get() = bal.get()
    override fun withdraw(amount: Int): Boolean {
        if (bal.get() < amount) return false     // each call is atomic...
        Thread.sleep(1)
        bal.addAndGet(-amount)                    // ...but the pair is not
        return true
    }
}

class CasAccount(start: Int) : Account {
    private val bal = AtomicInteger(start)
    override val balance get() = bal.get()
    override fun withdraw(amount: Int): Boolean {
        while (true) {
            val old = bal.get()
            if (old < amount) return false
            Thread.sleep(1)
            if (bal.compareAndSet(old, old - amount)) return true  // only if nobody got there first
        }
    }
}

fun trial(acct: Account): String {
    val approved = AtomicInteger()
    val gate = CountDownLatch(1)
    val ts = List(10) { thread { gate.await(); if (acct.withdraw(20)) approved.incrementAndGet() } }
    gate.countDown(); ts.forEach { it.join() }
    return "approved ${approved.get()}, balance ${acct.balance}"
}

fun main() {
    repeat(3) { run ->
        println("run ${run + 1}: synchronized: ${trial(SyncAccount(100))} | " +
                "atomic get+add: ${trial(AtomicButWrong(100))} | CAS loop: ${trial(CasAccount(100))}")
    }
}
