// The heap: each object's name -> the names it refers to.
fun leakPath(target: String, roots: List<String>, heap: Map<String, List<String>>): List<String>? {
    val parent = HashMap<String, String?>()
    val stack = ArrayDeque<String>()
    for (r in roots) if (r !in parent) { parent[r] = null; stack.addLast(r) }
    while (stack.isNotEmpty()) {
        val obj = stack.removeLast()            // a stack, so this is DFS
        for (next in heap.getValue(obj)) {
            if (next !in parent) { parent[next] = obj; stack.addLast(next) }
        }
    }
    if (target !in parent) return null
    return generateSequence(target) { parent[it] }.toList().reversed()
}
