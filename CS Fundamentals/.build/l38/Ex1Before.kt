// Exercise 1 starting point: a fines statement that does everything in one function.
fun statement(csv: String): String {
    val sb = StringBuilder()
    var total = 0
    for (line in csv.lines().filter { it.isNotBlank() }) {
        val (member, title, daysLate) = line.split(";")            // parse
        val fine = minOf(daysLate.trim().toInt() * 25, 500)        // rule: 25 cents a day, capped at 5.00
        total += fine
        sb.append("${member.padEnd(6)} ${title.padEnd(10)} ${"%d.%02d".format(fine / 100, fine % 100)}\n")  // format
    }
    sb.append("TOTAL ${"%d.%02d".format(total / 100, total % 100)}")
    return sb.toString()
}

fun main() = println(statement("Ada;Dune;3\nAlan;Mort;40\nGrace;Emma;0\n"))
