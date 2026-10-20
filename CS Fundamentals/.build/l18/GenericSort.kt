// Natural order: T must know how to compare itself (L13's upper bound).
fun <T : Comparable<T>> insertionSort(a: MutableList<T>) = insertionSort(a, naturalOrder())

// Any order: the caller passes the rule as a Comparator (L14's function-as-value).
fun <T> insertionSort(a: MutableList<T>, cmp: Comparator<in T>) {
    for (i in 1 until a.size) {
        val key = a[i]
        var j = i - 1
        while (j >= 0 && cmp.compare(key, a[j]) < 0) {
            a[j + 1] = a[j]
            j--
        }
        a[j + 1] = key
    }
}

data class Song(val title: String, val seconds: Int)

fun main() {
    val words = mutableListOf("pear", "fig", "kiwi", "apple", "date")
    insertionSort(words)
    println(words)
    insertionSort(words, compareBy<String> { it.length }.thenByDescending { it })
    println(words)

    val songs = mutableListOf(Song("Hey", 210), Song("Run", 185), Song("Go", 210))
    insertionSort(songs, compareBy { it.seconds })
    println(songs.map { it.title })
}
