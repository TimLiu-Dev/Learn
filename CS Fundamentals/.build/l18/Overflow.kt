// A pretend sorted array of 2 billion elements: element i is 2 * i.
// Too big to allocate (8 GB of longs), but the index arithmetic is identical.
class Huge(val size: Int) {
    operator fun get(i: Int): Long {
        if (i < 0 || i >= size) throw IndexOutOfBoundsException("Index $i out of bounds for length $size")
        return 2L * i
    }
}

fun searchBroken(a: Huge, target: Long): Int {
    var lo = 0
    var hi = a.size - 1
    while (lo <= hi) {
        val mid = (lo + hi) / 2                  // the textbook line
        if (a[mid] < target) lo = mid + 1 else if (a[mid] > target) hi = mid - 1 else return mid
    }
    return -1
}

fun searchFixed(a: Huge, target: Long): Int {
    var lo = 0
    var hi = a.size - 1
    while (lo <= hi) {
        val mid = lo + (hi - lo) / 2             // hi - lo can't overflow
        if (a[mid] < target) lo = mid + 1 else if (a[mid] > target) hi = mid - 1 else return mid
    }
    return -1
}

fun main() {
    println(1_500_000_000 + 1_999_999_999)                  // Int overflow (L2)
    println((1_500_000_000 + 1_999_999_999) ushr 1)         // the JDK's fix: unsigned shift
    val a = Huge(2_000_000_000)
    println("fixed:  ${searchFixed(a, 3_800_000_000)}")
    println("broken: ${searchBroken(a, 3_800_000_000)}")
}
