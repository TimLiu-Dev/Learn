import java.util.Random

fun randomArray(n: Int): IntArray {
    val rnd = Random(42)                         // fixed seed: same data every run
    return IntArray(n) { rnd.nextInt(1_000_000) }
}

fun count(input: IntArray, sort: (IntArray) -> Unit): Long {
    val a = input.copyOf()
    Count.compares = 0
    sort(a)
    check(a.contentEquals(input.sortedArray())) { "not sorted!" }
    return Count.compares
}

fun main() {
    for ((name, sort) in listOf<Pair<String, (IntArray) -> Unit>>(
        "insertion" to ::insertionSort, "merge" to ::mergeSort, "quick" to { a -> quickSort(a) })) {
        val a = intArrayOf(5, 2, 4, 6, 1, 3)
        sort(a)
        println("%-9s %s".format(name, a.contentToString()))
    }
    println()
    println("     n | insertion: random  sorted | merge: random  sorted | quick: random     sorted")
    for (n in listOf(1_000, 2_000, 4_000)) {
        val random = randomArray(n)
        val sorted = random.sortedArray()
        println("%,6d | %,17d %,7d | %,13d %,7d | %,13d %,10d".format(n,
            count(random, ::insertionSort), count(sorted, ::insertionSort),
            count(random, ::mergeSort), count(sorted, ::mergeSort),
            count(random) { quickSort(it) }, count(sorted) { quickSort(it) }))
    }
    try {
        count(randomArray(100_000).sortedArray()) { quickSort(it) }
    } catch (e: StackOverflowError) {
        println("quick on 100,000 sorted items: StackOverflowError")
    }
}
