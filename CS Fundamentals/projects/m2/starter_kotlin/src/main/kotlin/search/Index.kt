package search

import java.nio.file.Path

/**
 * The inverted index: term → postings (document, term frequency), plus whatever the commands need.
 * Design it yourself; the milestones on the project page say what each command needs.
 */
class Index {
    val documentCount: Int get() = TODO("documentCount (milestone 1)")
    val tokenCount: Long get() = TODO("tokenCount (milestone 1)")
    val termCount: Int get() = TODO("termCount (milestone 1)")

    companion object {
        /** Loads every regular *.txt file directly inside [dir]. */
        fun load(dir: Path): Index = TODO("Index.load (milestone 1): read the *.txt files, tokenise, count")
    }
}
