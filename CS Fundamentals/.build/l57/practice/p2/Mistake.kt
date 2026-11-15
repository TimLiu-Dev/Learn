sealed interface Step { val p: Int }
data class Local(override val p: Int) : Step
data class Send(override val p: Int, val msg: String) : Step
data class Recv(override val p: Int, val msg: String) : Step

// A receive that ignores the receiver's own clock.
fun lamportStamps(script: List<Step>): List<Long> {
    val clock = LongArray(3)
    val stampOf = mutableMapOf<String, Long>()
    return script.map { s ->
        clock[s.p] = when (s) {
            is Local -> clock[s.p] + 1
            is Send -> (clock[s.p] + 1).also { stampOf[s.msg] = it }
            is Recv -> stampOf.getValue(s.msg) + 1
        }
        clock[s.p]
    }
}

enum class Order { BEFORE, AFTER, EQUAL, CONCURRENT }

fun compare(a: List<Int>, b: List<Int>): Order {
    val le = a.indices.all { a[it] <= b[it] }
    val ge = a.indices.all { a[it] >= b[it] }
    return when {
        le && ge -> Order.EQUAL
        le -> Order.BEFORE
        ge -> Order.AFTER
        else -> Order.CONCURRENT
    }
}
