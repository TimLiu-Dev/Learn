fun main() {
    val deque = ArrayDeque<String>()                // no import: kotlin.collections.ArrayDeque
    println(deque.javaClass.name)
    deque.addLast("a"); deque.addLast("b"); deque.addLast("c")
    println("$deque pop: ${deque.removeLast()} peek: ${deque.lastOrNull()}")

    val list = mutableListOf("a", "b", "c")         // any MutableList can act as a stack too
    list.add("d")
    println("$list pop: ${list.removeAt(list.lastIndex)}")

    val jdk = java.util.ArrayDeque<String>()        // the JDK one needs its full name or an import
    jdk.push("a"); jdk.push("b")
    println("$jdk ${jdk.javaClass.name}")
}
