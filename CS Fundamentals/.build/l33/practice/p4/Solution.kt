// Masks personal data in a log line before it's written.

// local part @ domain labels . a top-level domain of 2+ letters. Stops before trailing punctuation.
val EMAIL = Regex("""[A-Za-z0-9._%+-]+@[A-Za-z0-9-]+(\.[A-Za-z0-9-]+)*\.[A-Za-z]{2,}""")

// 13 to 19 digits with optional single separators. \b stops it matching inside a longer digit run.
val CARD = Regex("""\b\d(?:[ -]?\d){12,18}\b""")

fun redact(line: String): String = line
    .replace(EMAIL, "[email]")
    .replace(CARD) { m -> "[card ****" + m.value.filter { it.isDigit() }.takeLast(4) + "]" }
