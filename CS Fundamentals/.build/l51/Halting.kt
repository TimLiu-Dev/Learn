// Programs that count their own steps, so we can stop watching them after a budget.
class OutOfFuel : RuntimeException(null, null, false, false)   // no stack trace: it's thrown often
class Fuel(private var left: Long) { fun tick() { if (--left < 0) throw OutOfFuel() } }

fun interface Program { fun run(fuel: Fuel) }

// A decider claims to answer "does p halt?" for EVERY program, and must itself always answer.
class Decider(val name: String, val halts: (Program) -> Boolean)

fun finishesWithin(p: Program, steps: Long): Boolean =
    try { p.run(Fuel(steps)); true } catch (e: OutOfFuel) { false }

// The diagonal program: ask d about yourself, then do the opposite.
fun contrarian(d: Decider): Program = object : Program {
    override fun run(fuel: Fuel) {
        if (d.halts(this)) {
            while (true) fuel.tick()          // d said "halts", so loop forever
        }                                     // d said "loops", so halt right now
    }
}

fun main() {
    val deciders = listOf(
        Decider("always says halts") { true },
        Decider("always says loops") { false },
        Decider("guesses from hashCode") { it.hashCode() % 2 == 0 },
        Decider("runs it for 1,000 steps") { finishesWithin(it, 1_000) },
    )
    for (d in deciders) {
        val p = contrarian(d)
        val verdict = try {
            if (d.halts(p)) "halts" else "loops"
        } catch (e: StackOverflowError) {
            "never answers (StackOverflowError)"
        }
        val actual = try {
            if (finishesWithin(p, 10_000_000)) "halts" else "still running after 10,000,000 steps"
        } catch (e: StackOverflowError) {
            "can't even start: it asks d first"
        }
        println("%-24s says %-35s actually: %s".format(d.name, verdict, actual))
    }
}
