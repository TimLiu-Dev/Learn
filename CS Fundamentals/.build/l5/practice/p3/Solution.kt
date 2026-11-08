class GradeBook(initial: List<Int>) {
    private val scores = initial.toMutableList()   // copy on the way in: our own list, still mutable

    fun add(score: Int) {
        scores.add(score)
    }

    val all: List<Int>
        get() = scores.toList()                     // a snapshot on the way out, never our list
}
