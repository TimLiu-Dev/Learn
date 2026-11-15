class Node<T>(val value: T, var next: Node<T>?)

// Floyd's "tortoise and hare": inside a loop, the fast walker catches up with the slow one.
fun <T> hasCycle(head: Node<T>?): Boolean {
    var slow = head
    var fast = head
    while (fast?.next != null) {            // fast reaching null means the list has an end
        slow = slow?.next
        fast = fast.next?.next
        if (slow === fast) return true      // the same node, not just an equal value
    }
    return false
}
