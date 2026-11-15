// The heap: each object's name -> the names it refers to.
fun leakPath(target: String, roots: List<String>, heap: Map<String, List<String>>): List<String>? =
    TODO("search from all the roots at once, remember who reached each object first, then walk back from target")
