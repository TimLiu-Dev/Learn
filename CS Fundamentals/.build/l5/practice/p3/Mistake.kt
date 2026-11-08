class GradeBook(initial: List<Int>) {
    private val scores = initial.toMutableList()

    fun add(score: Int) {
        scores.add(score)
    }

    val all: List<Int>
        get() = scores
}
