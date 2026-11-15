sealed interface Step { val p: Int }
data class Local(override val p: Int) : Step
data class Send(override val p: Int, val msg: String) : Step
data class Recv(override val p: Int, val msg: String) : Step

fun lamportStamps(script: List<Step>): List<Long> {
    val clock = LongArray(3)
    val stampOf = mutableMapOf<String, Long>()
    return script.map { s ->
        clock[s.p] = when (s) {
            is Local -> clock[s.p] + 1
            is Send -> (clock[s.p] + 1).also { stampOf[s.msg] = it }
            is Recv -> maxOf(clock[s.p], stampOf.getValue(s.msg)) + 1
        }
        clock[s.p]
    }
}

enum class Order { BEFORE, AFTER, EQUAL, CONCURRENT }

// Compares totals, as if a vector were one Lamport number.
fun compare(a: List<Int>, b: List<Int>): Order = when {
    a == b -> Order.EQUAL
    a.sum() < b.sum() -> Order.BEFORE
    a.sum() > b.sum() -> Order.AFTER
    else -> Order.CONCURRENT
}
