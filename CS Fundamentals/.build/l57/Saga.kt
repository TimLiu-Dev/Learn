// A saga: a sequence of local transactions, each with a compensating action that undoes it.

class Inventory(var seats: Int, var rooms: Int, var charged: Int = 0)

class Step(val name: String, val action: (Inventory) -> Boolean, val compensate: (Inventory) -> Unit)

fun runSaga(inv: Inventory, steps: List<Step>, afterEach: (String) -> Unit = {}): Boolean {
    val done = ArrayDeque<Step>()
    for (step in steps) {
        if (!step.action(inv)) {
            println("  ${step.name}: FAILED")
            while (done.isNotEmpty()) {                       // undo in reverse order
                val undo = done.removeLast()
                undo.compensate(inv)
                println("  compensate ${undo.name}")
            }
            return false
        }
        done.addLast(step)
        println("  ${step.name}: ok")
        afterEach(step.name)
    }
    return true
}

fun trip(cardWorks: Boolean) = listOf(
    Step("reserve flight", { if (it.seats > 0) { it.seats--; true } else false }, { it.seats++ }),
    Step("reserve hotel", { if (it.rooms > 0) { it.rooms--; true } else false }, { it.rooms++ }),
    Step("charge card", { if (cardWorks) { it.charged += 450; true } else false }, { it.charged -= 450 }),
)

fun main() {
    println("== Ana's trip: every step succeeds")
    val inv1 = Inventory(seats = 1, rooms = 1)
    println("  committed: ${runSaga(inv1, trip(cardWorks = true))}, seats=${inv1.seats} rooms=${inv1.rooms} charged=${inv1.charged}")

    println("== Ben's trip: the card is declined, so earlier steps are compensated")
    val inv2 = Inventory(seats = 1, rooms = 1)
    var seenByCleo = ""
    val ok = runSaga(inv2, trip(cardWorks = false)) { name ->
        if (name == "reserve hotel") seenByCleo = if (inv2.rooms == 0) "sold out" else "available"
    }
    println("  committed: $ok, seats=${inv2.seats} rooms=${inv2.rooms} charged=${inv2.charged}")
    println("  meanwhile Cleo searched hotels between Ben's steps and saw: $seenByCleo")
    println("  (no isolation: she saw a reservation that was later undone)")
}
