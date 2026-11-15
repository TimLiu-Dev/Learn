const val PAGE_BITS = 14
const val PAGE_SIZE = 1L shl PAGE_BITS

fun pageNumber(va: Long): Long = va ushr PAGE_BITS

fun offset(va: Long): Long = va and (PAGE_SIZE - 1)

// Looks up the frame but forgets the offset: every byte of a page lands on its first byte.
fun translate(va: Long, pageTable: Map<Long, Long>): Long? {
    val frame = pageTable[pageNumber(va)] ?: return null
    return frame shl PAGE_BITS
}
