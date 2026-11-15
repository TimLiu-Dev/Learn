const val PAGE_BITS = 14                     // 2^14 = 16384 bytes, this Mac's page size
const val PAGE_SIZE = 1L shl PAGE_BITS

// Which virtual page holds this address?
fun pageNumber(va: Long): Long = va ushr PAGE_BITS

// Where inside its page is this address?
fun offset(va: Long): Long = va and (PAGE_SIZE - 1)

// pageTable maps a virtual page number to a physical frame number.
fun translate(va: Long, pageTable: Map<Long, Long>): Long? {
    val frame = pageTable[pageNumber(va)] ?: return null   // unmapped: a page fault
    return (frame shl PAGE_BITS) or offset(va)              // same offset, different page
}
