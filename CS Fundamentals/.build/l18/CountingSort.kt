data class Student(val name: String, val grade: Char)

// Counting sort by grade 'A'..'F'. It never compares two students:
// the grade itself is used as an array index.
fun countingSortByGrade(xs: List<Student>): List<Student> {
    val k = 'F' - 'A' + 1                          // 6 possible keys
    val count = IntArray(k + 1)
    for (s in xs) count[s.grade - 'A' + 1]++       // 1. count each key
    for (g in 1..k) count[g] += count[g - 1]       // 2. prefix sums: count[g] = first slot for key g
    val out = arrayOfNulls<Student>(xs.size)
    for (s in xs) out[count[s.grade - 'A']++] = s  // 3. place, left to right: stable
    return out.map { it!! }
}

fun main() {
    val students = listOf(
        Student("Ada", 'B'), Student("Ben", 'A'), Student("Cy", 'B'),
        Student("Di", 'A'), Student("Eve", 'C'), Student("Flo", 'A'))
    println(countingSortByGrade(students).joinToString(" ") { "${it.grade}:${it.name}" })
}
