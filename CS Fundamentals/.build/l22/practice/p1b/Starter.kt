const val MB = 1024L * 1024
const val SMALL_HEAP = 130_862_280L   // HotSpot's built-in MaxHeapSize default, about 125 MB
const val ALIGN = 8 * MB              // every heap size this JVM picked was a multiple of 8 MB

// Round n up to the next multiple of step (n itself if it already is one).
fun roundUp(n: Long, step: Long): Long = TODO("round up to a multiple of step")

// The maximum heap, in bytes, for a memory limit in bytes.
fun defaultMaxHeap(limit: Long, xmx: Long? = null, maxRamPercentage: Double = 25.0): Long =
    TODO("-Xmx wins; else the small-memory rule or the percentage; then roundUp")
