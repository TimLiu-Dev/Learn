const val PAGE_BITS = 14                     // 2^14 = 16384 bytes, this Mac's page size
const val PAGE_SIZE = 1L shl PAGE_BITS

// Which virtual page holds this address?
fun pageNumber(va: Long): Long = TODO("shift the offset bits away")

// Where inside its page is this address?
fun offset(va: Long): Long = TODO("keep only the low PAGE_BITS bits")

// pageTable maps a virtual page number to a physical frame number.
fun translate(va: Long, pageTable: Map<Long, Long>): Long? =
    TODO("frame * PAGE_SIZE + offset, or null if the page isn't mapped")
