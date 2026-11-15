// One step of a script: process p (0, 1 or 2) does a local event, sends msg, or receives msg.
sealed interface Step { val p: Int }
data class Local(override val p: Int) : Step
data class Send(override val p: Int, val msg: String) : Step
data class Recv(override val p: Int, val msg: String) : Step

// The Lamport time of every step, in script order.
fun lamportStamps(script: List<Step>): List<Long> {
    val clock = LongArray(3)
    val stampOf = mutableMapOf<String, Long>()          // message -> the stamp it carries
    return script.map { s ->
        clock[s.p] = when (s) {
            is Local -> clock[s.p] + 1                  // IR1
            is Send -> (clock[s.p] + 1).also { stampOf[s.msg] = it }
            is Recv -> maxOf(clock[s.p], stampOf.getValue(s.msg)) + 1   // IR2(b): never backwards
        }
        clock[s.p]
    }
}

enum class Order { BEFORE, AFTER, EQUAL, CONCURRENT }

// How the event stamped a relates to the event stamped b (vectors of the same length).
fun compare(a: List<Int>, b: List<Int>): Order {
    val le = a.indices.all { a[it] <= b[it] }
    val ge = a.indices.all { a[it] >= b[it] }
    return when {
        le && ge -> Order.EQUAL
        le -> Order.BEFORE
        ge -> Order.AFTER
        else -> Order.CONCURRENT                        // each is ahead somewhere
    }
}
