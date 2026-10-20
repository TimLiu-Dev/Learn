// Minimal stand-in for your L20 MyBST (unbalanced). Swap in your own.
class MyBST<K : Comparable<K>, V> {
    private class Node<K, V>(val key: K, var value: V) {
        var left: Node<K, V>? = null
        var right: Node<K, V>? = null
    }

    private var root: Node<K, V>? = null

    fun put(key: K, value: V) {
        if (root == null) { root = Node(key, value); return }
        var node = root!!
        while (true) {
            val cmp = key.compareTo(node.key)
            when {
                cmp == 0 -> { node.value = value; return }
                cmp < 0 -> node = node.left ?: run { node.left = Node(key, value); return }
                else -> node = node.right ?: run { node.right = Node(key, value); return }
            }
        }
    }

    operator fun get(key: K): V? {
        var node = root
        while (node != null) {
            val cmp = key.compareTo(node.key)
            node = when {
                cmp == 0 -> return node.value
                cmp < 0 -> node.left
                else -> node.right
            }
        }
        return null
    }
}
