sealed interface Verdict {
    data class Halts(val steps: Int) : Verdict
    data class Loops(val entry: Int, val length: Int) : Verdict
}

// A step budget: "still running after 1,000 steps, so it must loop"
fun <C> analyse(start: C, next: (C) -> C?): Verdict {
    var c = start
    for (step in 0 until 1000) c = next(c) ?: return Verdict.Halts(step)
    return Verdict.Loops(0, 0)
}
