const val MB = 1024L * 1024
const val SMALL_HEAP = 130_862_280L   // HotSpot's built-in MaxHeapSize default, about 125 MB
const val ALIGN = 8 * MB              // every heap size this JVM picked was a multiple of 8 MB

// Round n up to the next multiple of step (n itself if it already is one).
fun roundUp(n: Long, step: Long): Long = (n + step - 1) / step * step

// The maximum heap, in bytes, for a memory limit in bytes.
fun defaultMaxHeap(limit: Long, xmx: Long? = null, maxRamPercentage: Double = 25.0): Long {
    if (xmx != null) return roundUp(xmx, ALIGN)
    val small = (limit * 50.0 / 100).toLong()
    val big = (limit * maxRamPercentage / 100).toLong()
    val heap = if (small < SMALL_HEAP) small else maxOf(big, SMALL_HEAP)
    return roundUp(heap, ALIGN)
}
