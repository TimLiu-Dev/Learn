class GradeBook(private val scores: MutableList<Int>) {
    fun add(score: Int) {
        scores.add(score)
    }

    // A read-only type... but which list does it point at?
    val all: List<Int>
        get() = scores
}
