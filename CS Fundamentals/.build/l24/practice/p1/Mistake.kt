// The heap: each object's name -> the names it refers to.
fun mark(roots: List<String>, heap: Map<String, List<String>>): Set<String> {
    val marked = HashSet<String>()
    fun visit(obj: String) {
        if (marked.add(obj)) heap.getValue(obj).forEach { visit(it) }   // one stack frame per object
    }
    roots.forEach { visit(it) }
    return marked
}

fun sweep(roots: List<String>, heap: Map<String, List<String>>): Set<String> =
    heap.keys - mark(roots, heap)
