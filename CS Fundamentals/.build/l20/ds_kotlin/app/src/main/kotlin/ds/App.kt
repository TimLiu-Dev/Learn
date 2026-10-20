package ds

// "Smaller" means "comes out of the min-heap first": higher count, then alphabetical.
data class WordCount(val word: String, val count: Int) : Comparable<WordCount> {
    override fun compareTo(other: WordCount) =
        compareValuesBy(this, other, { -it.count }, { it.word })
}

fun main() {
    val text = "The cat sat on the mat. The mat was flat,\nand the cat was fat! A cat, a mat, a hat."
    val words = text.lowercase().split(Regex("[^a-z]+")).filter { it.isNotEmpty() }

    val counts = MyBST<String, Int>()
    for (w in words) counts.put(w, (counts[w] ?: 0) + 1)
    println("${words.size} words, ${counts.size} unique, tree height ${counts.height()}")
    println("sorted: ${counts.inOrderKeys()}")
    println("first:  ${counts.min()}")

    val heap = MyMinHeap<WordCount>()
    for (w in counts.inOrderKeys()) heap.add(WordCount(w, counts[w]!!))
    val top3 = List(3) { heap.poll()!! }
    println("top 3:  " + top3.joinToString(" ") { "${it.word}=${it.count}" })

    for (stop in listOf("a", "the", "on")) counts.delete(stop)   // leaf, two children, one child
    println("without a/the/on: ${counts.inOrderKeys()} (height ${counts.height()})")
}
