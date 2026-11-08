// A Turing machine: a finite set of states plus an unbounded tape it can read, write and move on.
const val BLANK = '_'

data class Rule(val write: Char, val move: Int, val next: String)    // move is -1 (left) or +1 (right)

sealed interface Outcome {
    data class Accepted(val steps: Long, val tape: String) : Outcome
    data class Rejected(val steps: Long) : Outcome
    data class StillRunning(val steps: Long) : Outcome               // we gave up, it didn't
}

class TuringMachine(private val rules: Map<Pair<String, Char>, Rule>, private val start: String) {
    fun run(input: String, maxSteps: Long): Outcome {
        val tape = HashMap<Int, Char>()                  // unbounded in both directions
        input.forEachIndexed { i, c -> tape[i] = c }
        var head = 0
        var state = start
        var steps = 0L
        while (true) {
            if (state == "accept") return Outcome.Accepted(steps, render(tape))
            if (steps == maxSteps) return Outcome.StillRunning(steps)
            val rule = rules[state to (tape[head] ?: BLANK)] ?: return Outcome.Rejected(steps) // no rule: reject
            tape[head] = rule.write
            head += rule.move
            state = rule.next
            steps++
        }
    }

    private fun render(tape: Map<Int, Char>): String {
        val cells = tape.filterValues { it != BLANK }.keys
        if (cells.isEmpty()) return ""
        return (cells.min()..cells.max()).map { tape[it] ?: BLANK }.joinToString("")
    }
}

// Rules written as "state symbol -> write move next", one per line
fun machine(start: String, table: String) = TuringMachine(
    table.trim().lines().associate { line ->
        val p = line.trim().split(Regex("\\s+"))           // p[2] is the arrow
        (p[0] to p[1][0]) to Rule(p[3][0], if (p[4] == "R") 1 else -1, p[5])
    }, start)

val increment = machine("right", """
    right 0 -> 0 R right
    right 1 -> 1 R right
    right _ -> _ L carry
    carry 1 -> 0 L carry
    carry 0 -> 1 L accept
    carry _ -> 1 L accept
""")

// 0^n 1^n: cross off one 0 (as X) and one matching 1 (as Y), go back, repeat
val zerosThenOnes = machine("q0", """
    q0 0 -> X R q1
    q0 Y -> Y R q3
    q0 _ -> _ R accept
    q1 0 -> 0 R q1
    q1 Y -> Y R q1
    q1 1 -> Y L q2
    q2 0 -> 0 L q2
    q2 Y -> Y L q2
    q2 X -> X R q0
    q3 Y -> Y R q3
    q3 _ -> _ R accept
""")

val forever = machine("loop", "loop _ -> _ R loop")

fun describe(o: Outcome) = when (o) {                    // exhaustive over the sealed type (L7)
    is Outcome.Accepted -> "accept after ${o.steps} steps, tape=${o.tape}"
    is Outcome.Rejected -> "reject after ${o.steps} steps"
    is Outcome.StillRunning -> "still running after ${o.steps} steps"
}

fun main() {
    for (s in listOf("1011", "111", "0")) println("increment $s: ${describe(increment.run(s, 1_000))}")
    for (s in listOf("", "01", "0011", "000111", "0010", "011", "10"))
        println("0^n1^n \"$s\": ${describe(zerosThenOnes.run(s, 1_000))}")
    for (n in listOf(10, 100, 1000)) {
        val o = zerosThenOnes.run("0".repeat(n) + "1".repeat(n), 10_000_000) as Outcome.Accepted
        println("0^n1^n n=$n: ${o.steps} steps")
    }
    println("forever: ${describe(forever.run("", 1_000_000))}")
}
