import java.util.concurrent.CountDownLatch
import java.util.concurrent.atomic.AtomicInteger
import kotlin.concurrent.thread

class Account(var balance: Int) {
    fun withdraw(amount: Int): Boolean {
        if (balance >= amount) {                 // check...
            Thread.sleep(1)                      // (a slow fraud check widens the window)
            balance -= amount                    // ...then act
            return true
        }
        return false
    }
}

fun main() {
    repeat(3) { run ->
        val acct = Account(100)
        val approved = AtomicInteger()
        val gate = CountDownLatch(1)
        val customers = List(10) {
            thread { gate.await(); if (acct.withdraw(20)) approved.incrementAndGet() }
        }
        gate.countDown()
        customers.forEach { it.join() }
        println("run ${run + 1}: approved ${approved.get()} withdrawals of 20, balance = ${acct.balance}")
    }
}
