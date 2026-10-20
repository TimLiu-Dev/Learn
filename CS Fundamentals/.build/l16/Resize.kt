fun main() {
    var data = arrayOfNulls<String>(2)        // capacity 2
    var size = 0
    data[size++] = "a"
    data[size++] = "b"                        // full: size == capacity
    val bigger = arrayOfNulls<String>(data.size * 2)
    data.copyInto(bigger, 0, 0, size)         // copy every element: O(n)
    data = bigger                             // re-point; the old array is garbage now
    data[size++] = "c"
    println("size=$size capacity=${data.size} ${data.contentToString()}")
}
