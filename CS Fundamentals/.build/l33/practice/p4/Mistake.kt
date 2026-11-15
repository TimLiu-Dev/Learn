// No word boundaries: the pattern also fires on the first 19 digits of a longer id.
val EMAIL = Regex("""[A-Za-z0-9._%+-]+@[A-Za-z0-9-]+(\.[A-Za-z0-9-]+)*\.[A-Za-z]{2,}""")

val CARD = Regex("""\d(?:[ -]?\d){12,18}""")

fun redact(line: String): String = line
    .replace(EMAIL, "[email]")
    .replace(CARD) { m -> "[card ****" + m.value.filter { it.isDigit() }.takeLast(4) + "]" }
