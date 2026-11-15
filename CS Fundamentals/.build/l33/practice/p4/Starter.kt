// Masks personal data in a log line before it's written.
val EMAIL = Regex("""\S+@\S+""")            // a first attempt: "anything with an @ in it"
val CARD = Regex("""TODO: a card-number pattern""")

fun redact(line: String): String = line
    .replace(EMAIL, "[email]")
    .replace(CARD) { m -> TODO("[card ****1234]: keep only the last four digits of m.value") }
