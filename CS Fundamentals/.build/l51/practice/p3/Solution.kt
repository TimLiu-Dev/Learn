sealed interface Verdict {
    data class Halts(val steps: Int) : Verdict
    data class Loops(val entry: Int, val length: Int) : Verdict
}

// Run a deterministic machine from `start`. next(c) is the configuration after c, or null if the machine halts.
// Always answers when there are finitely many configurations.
fun <C> analyse(start: C, next: (C) -> C?): Verdict {
    val firstSeen = HashMap<C, Int>()                  // configuration -> step it was first seen at
    var c = start
    var step = 0
    while (true) {
        val earlier = firstSeen.putIfAbsent(c, step)
        if (earlier != null) return Verdict.Loops(earlier, step - earlier)   // same config, same future
        c = next(c) ?: return Verdict.Halts(step)
        step++
    }
}
