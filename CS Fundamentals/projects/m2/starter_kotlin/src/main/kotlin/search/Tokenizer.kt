package search

/** The 50 stop words from the spec. They are never indexed and never searched for. */
val STOP_WORDS: Set<String> = setOf(
    "a", "an", "and", "are", "as", "at", "be", "but", "by", "for",
    "from", "had", "has", "have", "he", "her", "him", "his", "i", "in",
    "is", "it", "its", "me", "my", "of", "on", "or", "she", "so",
    "that", "the", "their", "them", "then", "there", "they", "this", "to", "was",
    "we", "were", "what", "when", "which", "who", "will", "with", "you", "your",
)

/** Turns text into index terms, exactly as the spec says. Used for documents and queries alike. */
fun tokenize(text: String): List<String> {
    // Milestone 1: split on anything that isn't a letter or digit, lower-case, drop stop words.
    // Milestone 2: apostrophes, ’ (U+2019), and code points instead of chars.
    TODO("tokenize (milestone 1)")
}
