// Exercise 1 solution: one reason to change per piece; same output as Ex1Before.
data class LateReturn(val member: String, val title: String, val daysLate: Int)

object LateReturnCsv {                                   // the input format
    fun parse(csv: String): List<LateReturn> = csv.lines().filter { it.isNotBlank() }.map { line ->
        val (member, title, days) = line.split(";")
        LateReturn(member, title, days.trim().toInt())
    }
}

object FinePolicy {                                      // the library's rule
    const val CENTS_PER_DAY = 25
    const val CAP_CENTS = 500
    fun fineCents(r: LateReturn) = minOf(r.daysLate * CENTS_PER_DAY, CAP_CENTS)
}

object StatementFormat {                                 // the printed layout
    private fun money(cents: Int) = "%d.%02d".format(cents / 100, cents % 100)
    fun render(rows: List<Pair<LateReturn, Int>>): String =
        rows.joinToString("") { (r, fine) -> "${r.member.padEnd(6)} ${r.title.padEnd(10)} ${money(fine)}\n" } +
            "TOTAL ${money(rows.sumOf { it.second })}"
}

fun statement(csv: String): String =
    StatementFormat.render(LateReturnCsv.parse(csv).map { it to FinePolicy.fineCents(it) })

fun main() = println(statement("Ada;Dune;3\nAlan;Mort;40\nGrace;Emma;0\n"))
