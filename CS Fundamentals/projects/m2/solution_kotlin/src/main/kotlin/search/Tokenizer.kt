package search

/** The 50 stop words from the spec. They are never indexed and never searched for. */
val STOP_WORDS: Set<String> = setOf(
    "a", "an", "and", "are", "as", "at", "be", "but", "by", "for",
    "from", "had", "has", "have", "he", "her", "him", "his", "i", "in",
    "is", "it", "its", "me", "my", "of", "on", "or", "she", "so",
    "that", "the", "their", "them", "then", "there", "they", "this", "to", "was",
    "we", "were", "what", "when", "which", "who", "will", "with", "you", "your",
)

/**
 * Turns text into index terms, exactly as the spec says. Used for documents and queries alike.
 * Works on code points, not chars, so a letter outside the BMP such as U+1D400 counts as one
 * letter rather than two lone surrogates (L2).
 */
fun tokenize(text: String): List<String> {
    val cps = text.replace('’', '\'').codePoints().toArray()
    val out = mutableListOf<String>()
    val current = StringBuilder()

    fun flush() {
        if (current.isEmpty()) return
        val token = current.toString().lowercase() // lowercase() always uses Locale.ROOT
        if (token !in STOP_WORDS) out += token
        current.clear()
    }

    for ((i, c) in cps.withIndex()) {
        when {
            Character.isLetterOrDigit(c) -> current.appendCodePoint(c)
            // A non-empty token means the previous code point was a letter or digit.
            c == '\''.code && current.isNotEmpty() && i + 1 < cps.size && Character.isLetterOrDigit(cps[i + 1]) ->
                current.append('\'')
            else -> flush()
        }
    }
    flush()
    return out
}
