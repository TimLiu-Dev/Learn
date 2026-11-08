// Two-phase commit: one coordinator, two participants, simulated in one JVM.

enum class TxState { WORKING, PREPARED, COMMITTED, ABORTED }

class Participant(val name: String, private val canCommit: Boolean) {
    var state = TxState.WORKING
        private set
    val holdsLocks get() = state == TxState.WORKING || state == TxState.PREPARED

    fun prepare(): Boolean {                     // phase 1: promise to commit if asked, keep the locks
        state = if (canCommit) TxState.PREPARED else TxState.ABORTED
        return canCommit
    }
    fun finish(commit: Boolean) { state = if (commit) TxState.COMMITTED else TxState.ABORTED }
    // Once PREPARED, a participant may neither commit nor abort on its own.
    fun read() = if (holdsLocks) "BLOCKED: row locked by an in-doubt transaction" else "ok"
}

class Coordinator(private val crashBeforePhase2: Boolean = false) {
    val log = mutableListOf<String>()            // its durable decision log

    fun run(tx: String, parts: List<Participant>) {
        val votes = parts.map { it.prepare() }   // a real coordinator would also time out silent participants
        val commit = votes.all { it }
        log += "$tx ${if (commit) "COMMIT" else "ABORT"}"
        println("  votes ${parts.zip(votes).joinToString { (p, v) -> "${p.name}=${if (v) "yes" else "no"}" }}" +
            " -> coordinator logs ${if (commit) "COMMIT" else "ABORT"}")
        if (crashBeforePhase2) { println("  coordinator CRASHES before phase 2"); return }
        parts.forEach { it.finish(commit) }
    }

    fun recover(tx: String, parts: List<Participant>) {
        val commit = log.last { it.startsWith(tx) }.endsWith("COMMIT")
        println("  coordinator restarts, reads its log: ${if (commit) "COMMIT" else "ABORT"}, tells everyone")
        parts.forEach { it.finish(commit) }
    }
}

fun show(parts: List<Participant>) = parts.joinToString("  ") { "${it.name}=${it.state}" }

fun main() {
    println("== 1. Both participants can commit")
    val ok = listOf(Participant("orders-db", true), Participant("stock-db", true))
    Coordinator().run("tx1", ok)
    println("  ${show(ok)}")

    println("== 2. stock-db can't (out of stock)")
    val no = listOf(Participant("orders-db", true), Participant("stock-db", false))
    Coordinator().run("tx2", no)
    println("  ${show(no)}")

    println("== 3. Both vote yes, then the coordinator crashes")
    val stuck = listOf(Participant("orders-db", true), Participant("stock-db", true))
    val coord = Coordinator(crashBeforePhase2 = true)
    coord.run("tx3", stuck)
    println("  ${show(stuck)}")
    for (s in listOf(1, 60, 3_600)) println("  after ${s}s, a reader on orders-db: ${stuck[0].read()}")
    println("  orders-db asks stock-db what to do: it is ${stuck[1].state} too, so neither knows the decision")
    coord.recover("tx3", stuck)
    println("  ${show(stuck)}   reader: ${stuck[0].read()}")
}
