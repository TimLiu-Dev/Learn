package search

import java.nio.file.Path
import kotlin.io.path.isRegularFile
import kotlin.io.path.listDirectoryEntries
import kotlin.io.path.name
import kotlin.io.path.readText
import kotlin.math.ln
import kotlin.math.roundToLong

/** One loaded document. [tf] maps each of its terms to how often it occurs. */
data class Document(val name: String, val length: Int, val tf: Map<String, Int>)

data class Posting(val doc: Int, val tf: Int)

data class Hit(val name: String, val score: Double)

/** Score descending (compared at the printed 4 dp), then name ascending. */
val RANKING: Comparator<Hit> = compareByDescending<Hit> { (it.score * 10_000).roundToLong() }.thenBy { it.name }

/** The inverted index: term → postings (document, term frequency). Built once, then read-only. */
class Index private constructor(namedTexts: List<Pair<String, String>>) {
    private val docs = ArrayList<Document>()
    private val byName = HashMap<String, Document>()
    private val postings = HashMap<String, MutableList<Posting>>()
    private val corpusFrequency = HashMap<String, Long>()
    private val trie = Trie()

    val documentCount get() = docs.size
    val termCount get() = postings.size
    var tokenCount = 0L
        private set

    init {
        for ((name, text) in namedTexts) add(name, text)
        corpusFrequency.forEach(trie::add)
    }

    private fun add(name: String, text: String) {
        val tokens = tokenize(text)
        val tf = tokens.groupingBy { it }.eachCount()
        val id = docs.size
        val doc = Document(name, tokens.size, tf)
        docs += doc
        byName[name] = doc
        // Documents arrive in id order, so every postings list stays sorted by document.
        for ((term, count) in tf) {
            postings.getOrPut(term) { mutableListOf() } += Posting(id, count)
            corpusFrequency.merge(term, count.toLong(), Long::plus)
        }
        tokenCount += tokens.size
    }

    fun document(name: String): Document? = byName[name]

    fun df(term: String): Int = postings[term]?.size ?: 0

    /** idf(t) = ln((1 + D) / (1 + df(t))) + 1: smoothed, so a term in every document still counts 1. */
    fun idf(term: String): Double = ln((1.0 + docs.size) / (1.0 + df(term))) + 1.0

    /**
     * Documents containing every term. Walks only the rarest term's postings and checks the
     * others with O(1) hash lookups: O(min df × |terms|), not O(documents).
     */
    fun and(terms: List<String>): List<Hit> {
        val byDf = terms.sortedBy(::df)
        val rarest = postings[byDf.first()] ?: return emptyList()
        return rarest.map { docs[it.doc] }
            .filter { d -> byDf.all { it in d.tf } }
            .map { Hit(it.name, score(it, terms)) }
    }

    /** Documents containing at least one term: the union of their postings, O(Σ df). */
    fun or(terms: List<String>): List<Hit> =
        terms.flatMapTo(LinkedHashSet()) { t -> postings[t].orEmpty().map { it.doc } }
            .map { Hit(docs[it].name, score(docs[it], terms)) }

    /** score(d) = Σ tf(t, d) × idf(t), summed in query order. */
    private fun score(d: Document, terms: List<String>): Double = terms.sumOf { (d.tf[it] ?: 0) * idf(it) }

    fun withPrefix(prefix: String): List<TermCount> = trie.withPrefix(prefix)

    fun termFrequencies(): Sequence<TermCount> = corpusFrequency.asSequence().map { TermCount(it.key, it.value) }

    companion object {
        /** Loads every regular `*.txt` file directly inside [dir], in name order. */
        fun load(dir: Path): Index =
            Index(dir.listDirectoryEntries("*.txt").filter { it.isRegularFile() }.sortedBy { it.name }
                .map { it.name.removeSuffix(".txt") to it.readText() })

        /** Builds an index from name → text: handy for unit tests. */
        fun of(namedTexts: Map<String, String>): Index = Index(namedTexts.toSortedMap().toList())
    }
}
