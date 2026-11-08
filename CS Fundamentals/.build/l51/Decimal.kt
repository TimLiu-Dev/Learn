// Accepts an optional sign, then digits, then optionally '.' and more digits.
// "12", "-3.5", "+0.25" yes; "", "-", "1.", ".5", "--2", "1.2.3" no.
enum class St { START, SIGN, INT, DOT, FRAC }

fun next(q: St, c: Char): St? {                      // null = the dead state
    val digit = c in '0'..'9'                       // not c.isDigit(): see below
    return when (q) {
        St.START -> if (c == '+' || c == '-') St.SIGN else if (digit) St.INT else null
        St.SIGN -> if (digit) St.INT else null
        St.INT -> if (digit) St.INT else if (c == '.') St.DOT else null
        St.DOT -> if (digit) St.FRAC else null
        St.FRAC -> if (digit) St.FRAC else null
    }
}

fun isDecimal(s: String): Boolean {
    var q: St? = St.START
    for (c in s) q = next(q ?: return false, c)
    return q == St.INT || q == St.FRAC
}

fun main() {
    val cases = listOf("12", "-3.5", "+0.25", "007", "", "-", "1.", ".5", "--2", "1.2.3", "\u0663")
    for (s in cases) println("%-8s %s".format("\"$s\"", isDecimal(s)))
    // Cross-check against a regex for the same language, on every string of length <= 5
    val alphabet = "1+-.\u0663"                   // includes an Arabic-Indic three
    val regex = Regex("""[+-]?\d+(\.\d+)?""")
    var all = listOf("")
    var checked = 0
    repeat(5) {
        all = all.flatMap { p -> alphabet.map { p + it } }
        for (s in all) { checked++; check(isDecimal(s) == regex.matches(s)) { s } }
    }
    println("agrees with the regex on all $checked strings")
}
