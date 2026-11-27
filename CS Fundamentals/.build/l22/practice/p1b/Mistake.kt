const val MB = 1024L * 1024
const val SMALL_HEAP = 130_862_280L   // HotSpot's built-in MaxHeapSize default, about 125 MB
const val ALIGN = 8 * MB              // every heap size this JVM picked was a multiple of 8 MB

fun roundUp(n: Long, step: Long): Long = (n + step - 1) / step * step

// The mistake: "the heap is 25% of the limit", full stop.
fun defaultMaxHeap(limit: Long, xmx: Long? = null, maxRamPercentage: Double = 25.0): Long {
    if (xmx != null) return roundUp(xmx, ALIGN)
    return roundUp((limit * maxRamPercentage / 100).toLong(), ALIGN)
}
