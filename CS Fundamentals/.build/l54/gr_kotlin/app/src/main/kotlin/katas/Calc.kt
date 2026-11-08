package katas

// One entry point per back end. Both turn a CalcError into a value, so tests can compare them.
sealed interface Outcome {
    data class Value(val n: Int) : Outcome
    data class Error(val message: String) : Outcome
}

private fun attempt(run: () -> Int): Outcome =
    try { Outcome.Value(run()) } catch (e: CalcError) { Outcome.Error(e.message ?: "") }

fun runInterpreted(src: String): Outcome = attempt { parse(src).also(::resolve).let(::interpret) }

fun runCompiled(src: String): Outcome = attempt { execute(Compiler().compile(parse(src))) }
