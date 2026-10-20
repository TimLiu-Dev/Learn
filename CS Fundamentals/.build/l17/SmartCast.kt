class Node<T>(val value: T, var next: Node<T>?)

class MyList<T> {
    var head: Node<T>? = null

    fun firstOrNull(): T? {
        val h = head                // copy the property into a local val...
        if (h != null) return h.value   // ...which nothing else can change
        return null
    }

    fun firstOrNullShort(): T? = head?.value    // or let ?. read it once
}

fun main() {
    val list = MyList<String>()
    println(list.firstOrNull())
    list.head = Node("A", null)
    println(list.firstOrNull() + " " + list.firstOrNullShort())
}
