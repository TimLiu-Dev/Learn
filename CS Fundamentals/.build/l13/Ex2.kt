// src only produces T (we remove and read), dst only consumes T (we add)
fun <T> transferFirst(src: MutableList<out T>, dst: MutableList<in T>) {
    dst.add(src.removeAt(0))
}

interface Producer<out T> { fun produce(): T }

class Greeter : Producer<String> {
    override fun produce() = "hi"
}

fun main() {
    val src = mutableListOf(1, 2, 3)
    val numbers = mutableListOf<Number>()
    val objects = mutableListOf<Any>()
    transferFirst(src, numbers)
    transferFirst(src, objects)
    println("src=$src numbers=$numbers objects=$objects")

    val p: Producer<Any> = Greeter()       // out T makes this legal
    println(p.produce())
}
