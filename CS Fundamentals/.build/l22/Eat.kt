fun main() {
    val maxMb = Runtime.getRuntime().maxMemory() / 1024 / 1024
    val chunks = mutableListOf<ByteArray>()
    try {
        while (true) chunks += ByteArray(8 * 1024 * 1024)        // 8 MB at a time, all kept reachable
    } catch (e: OutOfMemoryError) {
        val held = chunks.size * 8
        chunks.clear()                                          // drop them, so there's room to keep going
        println("max heap $maxMb MB; caught $e after $held MB")
    }
    println("still alive, exiting normally")
}
