import java.util.concurrent.ConcurrentHashMap
import java.util.concurrent.CopyOnWriteArrayList
import java.util.concurrent.atomic.AtomicBoolean

val t0 = System.currentTimeMillis()
private val printLock = Any()
fun log(msg: String) = synchronized(printLock) { println("  [%4d ms] %s".format(System.currentTimeMillis() - t0, msg)) }

fun main() {
    println("-- at-least-once: worker w2 crashes after sending an email, before acking it")
    emails(idempotent = false)
    println("-- the same crash, with an idempotent consumer")
    emails(idempotent = true)
    println("-- a poison message")
    poison()
    println("-- backpressure: a queue of 5, a producer faster than its consumer")
    backpressure()
}

fun emails(idempotent: Boolean) {
    JobQueue(capacity = 100, visibilityMs = 300, maxAttempts = 3).use { q ->
        repeat(6) { q.push("welcome email for user ${it + 1}") }
        val sent = CopyOnWriteArrayList<Long>()                 // the outside world: emails really sent
        val done = ConcurrentHashMap.newKeySet<Long>()          // stands in for a processed_ids table
        val crashed = AtomicBoolean(false)
        val workers = (1..3).map { w ->
            Thread.ofVirtual().start {
                while (true) {
                    val m = q.receive(500) ?: break
                    if (idempotent && m.id in done) { log("w$w skips #${m.id}: already done"); q.ack(m.id); continue }
                    sent += m.id
                    log("w$w sends #${m.id} (attempt ${m.attempts})")
                    if (idempotent) done += m.id
                    if (w == 2 && crashed.compareAndSet(false, true)) { log("w2 CRASHES before acking #${m.id}"); break }
                    q.ack(m.id)
                }
            }
        }
        workers.forEach { it.join() }
        println("  6 jobs -> ${sent.size} emails sent, duplicates: ${sent.groupingBy { it }.eachCount().filterValues { it > 1 }.keys}")
    }
}

fun poison() {
    JobQueue(capacity = 100, visibilityMs = 100, maxAttempts = 3).use { q ->
        listOf("ok 1", "BAD", "ok 2").forEach(q::push)
        while (true) {
            val m = q.receive(400) ?: break
            try {
                require(m.body != "BAD") { "cannot parse '${m.body}'" }
                log("#${m.id} '${m.body}' done")
                q.ack(m.id)
            } catch (e: IllegalArgumentException) {
                log("#${m.id} attempt ${m.attempts} failed: ${e.message}")        // no ack: it will come back
            }
        }
        println("  dead letters: ${q.deadLetters}")
    }
}

fun backpressure() {
    JobQueue(capacity = 5, visibilityMs = 1000, maxAttempts = 3).use { q ->
        val consumer = Thread.ofVirtual().start {
            while (true) { val m = q.receive(300) ?: break; Thread.sleep(50); q.ack(m.id) }
        }
        val start = System.currentTimeMillis()
        repeat(20) { q.push("job $it") }                         // blocks whenever 5 are waiting
        println("  push(): 20 jobs queued after ${System.currentTimeMillis() - start} ms (the producer was slowed down)")
        val rejected = (1..20).count { !q.offer("extra $it") }   // never blocks; says no instead
        println("  offer(): $rejected of 20 extra jobs rejected straight away")
        consumer.join()
    }
}
