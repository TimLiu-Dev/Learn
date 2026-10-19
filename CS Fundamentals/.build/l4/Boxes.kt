class Box(var value: Int)

fun grow(b: Box, n: Int): Int {
    b.value += n
    var m = n
    m *= 10
    return m
}

fun main() {
    val a = Box(1)
    val b = a
    val c = Box(1)
    val n = 5
    val result = grow(b, n)
    println("${a.value} ${b.value} ${c.value} $n $result")

    val boxes = listOf(a, c, a)
    boxes[2].value = 7
    println("${a.value} ${boxes[0].value} ${boxes[1].value}")
}
