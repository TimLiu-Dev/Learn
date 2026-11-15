// The heap: each object's name -> the names it refers to.
fun leakPath(target: String, roots: List<String>, heap: Map<String, List<String>>): List<String>? {
    val parent = HashMap<String, String?>()     // who reached each object first; null for a root
    val queue = ArrayDeque<String>()
    for (r in roots) if (r !in parent) { parent[r] = null; queue.addLast(r) }
    while (queue.isNotEmpty()) {                // BFS: objects come out in order of distance
        val obj = queue.removeFirst()
        if (obj == target) break
        for (next in heap.getValue(obj)) {
            if (next !in parent) { parent[next] = obj; queue.addLast(next) }
        }
    }
    if (target !in parent) return null          // unreachable: garbage, not a leak
    return generateSequence(target) { parent[it] }.toList().reversed()
}
