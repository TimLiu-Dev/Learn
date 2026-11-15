class DisjointSets(n: Int) {
    val parent = IntArray(n) { it }           // public so the checks can inspect the trees
    private val size = IntArray(n) { 1 }      // size[r] is only meaningful when r is a root

    fun find(p: Int): Int = TODO("walk to the root, then point every node on the path straight at it")

    // Returns false if p and q were already connected.
    fun union(p: Int, q: Int): Boolean = TODO("link the smaller tree's root under the larger one's")

    fun sizeOf(p: Int): Int = TODO("the number of sites in p's component")
}
