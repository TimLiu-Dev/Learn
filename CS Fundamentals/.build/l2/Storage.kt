import java.math.BigDecimal

fun unitBytes(unit: String): Long =
    if (unit == "KiB") 1024L
    else if (unit == "MiB") 1024L * 1024
    else 1024L * 1024 * 1024

fun main() {
    val inputs = listOf("512 MiB", "2 GiB", "4 GiB", "1.5 KiB", "bogus")
    val pattern = Regex("""(\d+(?:\.\d+)?)\s*(KiB|MiB|GiB)""")
    var total = 0L

    for (input in inputs) {
        val match = pattern.matchEntire(input)
        if (match == null) {
            println("can't parse: $input")
            continue
        }
        val number = BigDecimal(match.groupValues[1])
        val bytes = (number * BigDecimal(unitBytes(match.groupValues[2]))).toLong()
        println("$input = $bytes bytes")
        total += bytes
    }

    val gib = total / (1024.0 * 1024 * 1024)
    println("total = $total bytes (${"%.2f".format(gib)} GiB)")
}
