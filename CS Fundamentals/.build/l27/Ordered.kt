// Fix 1: always lock the account with the smaller id first. No cycle can form.
import kotlin.concurrent.thread

class Acct(val id: Int) {
    var balance = 1_000_000L
}

fun transfer(from: Acct, to: Acct, amount: Long) {
    val (first, second) = if (from.id < to.id) from to to else to to from  // a global order: by id
    synchronized(first) {
        synchronized(second) {
            from.balance -= amount
            to.balance += amount
        }
    }
}

fun main() {
    val alice = Acct(1)
    val bob = Acct(2)
    val start = System.nanoTime()
    val t1 = thread { repeat(1_000_000) { transfer(alice, bob, 1) } }
    val t2 = thread { repeat(1_000_000) { transfer(bob, alice, 1) } }
    t1.join(); t2.join()
    println("2,000,000 opposite transfers in ${(System.nanoTime() - start) / 1_000_000} ms")
    println("alice=${alice.balance} bob=${bob.balance} total=${alice.balance + bob.balance}")
}
