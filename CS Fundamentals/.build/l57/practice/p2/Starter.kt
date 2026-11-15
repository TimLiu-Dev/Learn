// One step of a script: process p (0, 1 or 2) does a local event, sends msg, or receives msg.
sealed interface Step { val p: Int }
data class Local(override val p: Int) : Step
data class Send(override val p: Int, val msg: String) : Step
data class Recv(override val p: Int, val msg: String) : Step

// The Lamport time of every step, in script order.
fun lamportStamps(script: List<Step>): List<Long> =
    TODO("one counter per process; a Send remembers its stamp, a Recv jumps past it")

enum class Order { BEFORE, AFTER, EQUAL, CONCURRENT }

// How the event stamped a relates to the event stamped b (vectors of the same length).
fun compare(a: List<Int>, b: List<Int>): Order =
    TODO("BEFORE only if a is <= b in every entry and they differ somewhere")
