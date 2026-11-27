package search

private const val SHOWN_RESULTS = 10
private const val SUGGESTIONS = 5

/** Runs one command line against an index and returns its output, without the trailing blank line. */
class Commands(private val index: Index) {

    fun run(line: String): String {
        val parts = line.trim().split(Regex("\\s+"), limit = 2)
        val rest = parts.getOrElse(1) { "" }
        return when (val word = parts[0]) {
            "tokens" -> tokenize(rest).joinToString(" ").ifEmpty { "(none)" }
            "stats" -> stats()
            "doc" -> doc(rest)
            "q" -> query(rest, and = true)
            "or" -> query(rest, and = false)
            "prefix" -> prefix(rest)
            "top" -> top(rest)
            "bench" -> bench(rest)
            else -> "error: unknown command '$word'"
        }
    }

    private fun stats(): String {
        val avg = if (index.documentCount == 0) 0.0 else index.tokenCount.toDouble() / index.documentCount
        return """
            documents: ${index.documentCount}
            tokens: ${index.tokenCount}
            terms: ${index.termCount}
            average document length: ${"%.2f".format(java.util.Locale.ROOT, avg)}
        """.trimIndent()
    }

    private fun doc(name: String): String {
        if (name.isEmpty()) return "error: usage: doc <name>"
        val d = index.document(name) ?: return "error: no document named '$name'"
        val top = d.tf.map { TermCount(it.key, it.value.toLong()) }.topK(3, BY_COUNT)
            .joinToString(", ") { "${it.term} (${it.count})" }
        return """
            doc: ${d.name}
            tokens: ${d.length}
            terms: ${d.tf.size}
            top terms: ${top.ifEmpty { "(none)" }}
        """.trimIndent()
    }

    private fun query(text: String, and: Boolean): String {
        val terms = queryTerms(text)
        if (terms.isEmpty()) return "error: no searchable terms in query"
        val hits = if (and) index.and(terms) else index.or(terms)
        val lines = hits.topK(SHOWN_RESULTS, RANKING).mapIndexed { i, h ->
            "${i + 1}. ${h.name} ${"%.4f".format(java.util.Locale.ROOT, h.score)}"
        }
        return (listOf("results: ${hits.size}") + lines).joinToString("\n")
    }

    private fun prefix(text: String): String {
        if (text.isEmpty() || text.any { it.isWhitespace() }) return "error: usage: prefix <p>"
        val best = index.withPrefix(text.replace('’', '\'').lowercase()).topK(SUGGESTIONS, BY_COUNT)
        return if (best.isEmpty()) "no suggestions" else numbered(best)
    }

    private fun top(text: String): String {
        val k = text.toIntOrNull()?.takeIf { it >= 1 } ?: return "error: usage: top <k> (k is a positive integer)"
        val best = index.termFrequencies().asIterable().topK(k, BY_COUNT)
        return if (best.isEmpty()) "no terms" else numbered(best)
    }

    private fun bench(text: String): String {
        val parts = text.split(Regex("\\s+"), limit = 2)
        val reps = parts[0].toIntOrNull()?.takeIf { it >= 1 }
        if (reps == null || parts.size < 2) return "error: usage: bench <reps> <query>"
        val terms = queryTerms(parts[1])
        if (terms.isEmpty()) return "error: no searchable terms in query"
        var sink = 0L
        repeat(reps) { sink += runOnce(terms) } // warm-up: let the JIT compile the query path (L16)
        val start = System.nanoTime()
        repeat(reps) { sink += runOnce(terms) }
        val elapsed = System.nanoTime() - start
        blackhole = sink
        return "bench: %d queries, %d results, %.3f ms".format(java.util.Locale.ROOT, reps, index.and(terms).size, elapsed / 1e6)
    }

    private fun runOnce(terms: List<String>): Int {
        val hits = index.and(terms)
        return hits.size + hits.topK(SHOWN_RESULTS, RANKING).size
    }

    private fun numbered(entries: List<TermCount>) =
        entries.mapIndexed { i, e -> "${i + 1}. ${e.term} ${e.count}" }.joinToString("\n")

    private companion object {
        /** Where bench leaves its results, so the JIT can't skip the work as dead code (L16). */
        @Volatile
        var blackhole = 0L

        /** Query terms: tokenised like documents, duplicates dropped, first occurrence kept. */
        fun queryTerms(text: String) = tokenize(text).distinct()
    }
}
