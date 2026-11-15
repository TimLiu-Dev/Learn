// The heap: each object's name -> the names it refers to.
fun mark(roots: List<String>, heap: Map<String, List<String>>): Set<String> {
    val marked = HashSet<String>()
    val stack = ArrayDeque(roots)              // the mark stack: reached, not yet scanned
    while (stack.isNotEmpty()) {
        val obj = stack.removeLast()
        // add() returns false for an object already marked, so a cycle stops here.
        if (marked.add(obj)) stack.addAll(heap.getValue(obj))
    }
    return marked
}

fun sweep(roots: List<String>, heap: Map<String, List<String>>): Set<String> =
    heap.keys - mark(roots, heap)
