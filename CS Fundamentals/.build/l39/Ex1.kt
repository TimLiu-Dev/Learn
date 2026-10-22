import java.util.concurrent.CopyOnWriteArrayList

enum class Tier { CHILD, STUDENT, ADULT }
data class Loan(val title: String, val daysLate: Int, val tier: Tier)
typealias LateFeePolicy = (Loan) -> Int

// (a) one strategy per tier, chosen by a map instead of a when
val byTier: Map<Tier, LateFeePolicy> = mapOf(
    Tier.CHILD to { _: Loan -> 0 },
    Tier.STUDENT to { l: Loan -> minOf(l.daysLate * 10, 300) },
    Tier.ADULT to { l: Loan -> l.daysLate * 25 },
)

fun fee(loan: Loan, amnestyWeek: Boolean): Int {
    val policy: LateFeePolicy = if (amnestyWeek) { _ -> 0 } else byTier.getValue(loan.tier)   // a new rule, no other edits
    return policy(loan)
}

// (b) a listener that removes itself after the first event
class Desk {
    private val listeners = CopyOnWriteArrayList<(String) -> Unit>()
    fun subscribe(l: (String) -> Unit) { listeners += l }
    fun subscribeOnce(l: (String) -> Unit) {
        lateinit var wrapper: (String) -> Unit          // the lambda needs a name to remove itself
        wrapper = { e -> listeners -= wrapper; l(e) }
        listeners += wrapper
    }
    fun publish(e: String) = listeners.forEach { it(e) }
    val count get() = listeners.size
}

fun main() {
    val loans = listOf(Loan("Gruffalo", 9, Tier.CHILD), Loan("SICP", 40, Tier.STUDENT), Loan("Dune", 3, Tier.ADULT))
    for (l in loans) println("${l.title}: ${fee(l, false)} (amnesty: ${fee(l, true)})")
    val desk = Desk()
    desk.subscribe { println("always: $it") }
    desk.subscribeOnce { println("once:   $it") }
    desk.publish("Dune lent")
    desk.publish("Emma lent")
    println("listeners left: ${desk.count}")
}
