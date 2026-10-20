class Box<T>(val item: T)

fun main() {
    val strings = ArrayList<String>()
    val ints = ArrayList<Int>()
    println(strings.javaClass.name)
    println(strings.javaClass == ints.javaClass)      // one class at runtime
    println(Box("a").javaClass == Box(1).javaClass)

    val box = Box("hi")
    val s: String = box.item               // compiler inserts a hidden cast here
    println(s)
}
