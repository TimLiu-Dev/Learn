package ds

// A map kept in binary-search-tree order. Not balanced: sorted input makes it a list.
class MyBST<K : Comparable<K>, V> {
    private class Node<K, V>(var key: K, var value: V) {
        var left: Node<K, V>? = null
        var right: Node<K, V>? = null
    }

    private var root: Node<K, V>? = null
    var size = 0
        private set

    fun put(key: K, value: V) { root = put(root, key, value) }

    private fun put(n: Node<K, V>?, key: K, value: V): Node<K, V> {
        if (n == null) { size++; return Node(key, value) }
        val c = key.compareTo(n.key)
        when {
            c < 0 -> n.left = put(n.left, key, value)
            c > 0 -> n.right = put(n.right, key, value)
            else -> n.value = value                      // existing key: replace the value
        }
        return n
    }

    private fun find(key: K): Node<K, V>? {
        var n = root
        while (n != null) {
            val c = key.compareTo(n.key)
            n = when {
                c < 0 -> n.left
                c > 0 -> n.right
                else -> return n
            }
        }
        return null
    }

    operator fun get(key: K): V? = find(key)?.value
    operator fun contains(key: K): Boolean = find(key) != null

    fun delete(key: K) {
        if (key !in this) return
        root = delete(root, key)
        size--
    }

    private fun delete(n: Node<K, V>?, key: K): Node<K, V>? {
        if (n == null) return null
        val c = key.compareTo(n.key)
        if (c < 0) n.left = delete(n.left, key)
        else if (c > 0) n.right = delete(n.right, key)
        else {
            val left = n.left ?: return n.right          // case 1 (leaf) or 2 (right child only)
            val right = n.right ?: return left           // case 2 (left child only)
            var succ = right                             // case 3: in-order successor
            while (true) succ = succ.left ?: break
            n.key = succ.key
            n.value = succ.value
            n.right = delete(right, succ.key)
        }
        return n
    }

    fun min(): K? {
        var n = root ?: return null
        while (true) n = n.left ?: return n.key
    }

    fun height(): Int = height(root)
    private fun height(n: Node<K, V>?): Int =
        if (n == null) 0 else 1 + maxOf(height(n.left), height(n.right))

    fun inOrderKeys(): List<K> {
        val out = mutableListOf<K>()
        fun walk(n: Node<K, V>?) {                       // a local function can see `out`
            if (n == null) return
            walk(n.left); out += n.key; walk(n.right)
        }
        walk(root)
        return out
    }
}
