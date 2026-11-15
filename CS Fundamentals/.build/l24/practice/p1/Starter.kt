// The heap: each object's name -> the names it refers to.
fun mark(roots: List<String>, heap: Map<String, List<String>>): Set<String> =
    TODO("start from every root and follow references until nothing new turns up")

fun sweep(roots: List<String>, heap: Map<String, List<String>>): Set<String> =
    TODO("every object in the heap that mark() didn't reach")
