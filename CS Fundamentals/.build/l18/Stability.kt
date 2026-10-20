import java.util.Collections

data class Student(val name: String, val grade: Char)

// Same Lomuto quicksort as before, made generic with a Comparator.
fun <T> quickSortBy(a: MutableList<T>, cmp: Comparator<in T>, lo: Int = 0, hi: Int = a.size - 1) {
    if (lo >= hi) return
    val pivot = a[hi]
    var i = lo
    for (j in lo until hi) {
        if (cmp.compare(a[j], pivot) < 0) { Collections.swap(a, i, j); i++ }
    }
    Collections.swap(a, i, hi)
    quickSortBy(a, cmp, lo, i - 1)
    quickSortBy(a, cmp, i + 1, hi)
}

fun show(label: String, xs: List<Student>) =
    println(label.padEnd(10) + xs.joinToString(" ") { "${it.grade}:${it.name}" })

fun main() {
    // Already in alphabetical order by name.
    val students = listOf(
        Student("Ada", 'B'), Student("Ben", 'A'), Student("Cy", 'B'),
        Student("Di", 'A'), Student("Eve", 'C'), Student("Flo", 'A'))
    show("input", students)
    show("stable", students.sortedBy { it.grade })            // JDK: stable
    val q = students.toMutableList()
    quickSortBy(q, compareBy { it.grade })                     // ours: not stable
    show("quick", q)
}
