class Node<T>(val value: T, var next: Node<T>?)

class MyList<T> {
    var head: Node<T>? = null

    fun firstOrNull(): T? {
        if (head != null) return head.value     // head is a var PROPERTY
        return null
    }
}
