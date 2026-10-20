fun main() {
    val vocabulary = listOf("the", "the", "the", "cat", "cat", "mat", "a", "hat", "sat", "on")
    val rnd = java.util.Random(15)                                 // same seed, same text, every run
    val words = generateSequence { vocabulary[rnd.nextInt(vocabulary.size)] }   // endless "text"

    val seen = HashMap<String, Int>()                              // state the lambda updates
    var read = 0
    val winner = words
        .onEach { read++ }
        .first { word -> seen.merge(word, 1, Int::plus) == 100 }   // stop at the 100th sighting

    println("'$winner' reached 100 first, after reading $read words")
    println("counts then: ${seen.toSortedMap()}")
}
