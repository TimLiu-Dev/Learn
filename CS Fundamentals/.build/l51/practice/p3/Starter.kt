sealed interface Verdict {
    data class Halts(val steps: Int) : Verdict
    data class Loops(val entry: Int, val length: Int) : Verdict
}

// Run a deterministic machine from `start`. next(c) is the configuration after c, or null if the machine halts.
// Always answers when there are finitely many configurations.
fun <C> analyse(start: C, next: (C) -> C?): Verdict = TODO("remember the step at which each configuration was first seen")
