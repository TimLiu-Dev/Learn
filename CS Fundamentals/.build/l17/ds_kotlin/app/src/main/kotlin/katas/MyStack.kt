package katas

class MyStack<T> {
    private val list = MyLinkedList<T>()            // the top of the stack is the head

    fun push(item: T) = list.addFirst(item)         // O(1)
    fun pop(): T = list.removeFirst()               // O(1); throws when empty
    fun peek(): T? = list.peekFirst()               // O(1); null when empty
    fun isEmpty() = list.isEmpty()
    val size get() = list.size
}
