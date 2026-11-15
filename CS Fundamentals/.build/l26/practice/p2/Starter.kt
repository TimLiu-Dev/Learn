// Each thread runs count++ as three steps: 0 = read, 1 = add, 2 = write.
// schedule[k] is the thread ('A' or 'B') that takes its next step.
fun finalCount(start: Int, schedule: String): Int {
    TODO("give each thread its own register and step number, then follow the schedule")
}

// Every string with exactly a 'A's and b 'B's, e.g. allSchedules(1, 1) = [AB, BA].
fun allSchedules(a: Int, b: Int): List<String> {
    TODO("recursion: a schedule starts with A (if A has steps left) or with B")
}
