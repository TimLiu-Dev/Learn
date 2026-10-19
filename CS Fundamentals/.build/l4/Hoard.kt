fun main() {
    val hoard = mutableListOf<ByteArray>()
    while (true) {
        hoard.add(ByteArray(1_000_000))   // 1 MB each, all still reachable
        if (hoard.size % 10 == 0) println("holding ${hoard.size} MB")
    }
}
