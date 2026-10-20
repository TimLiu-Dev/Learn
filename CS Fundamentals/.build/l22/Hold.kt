fun main() {
    val big = ByteArray(200 * 1024 * 1024)        // 200 MB on the heap, all zeros
    println("pid ${ProcessHandle.current().pid()} holding ${big.size / 1024 / 1024} MB; inspect me now")
    Thread.sleep(60_000)
    println(big[0])                               // keep 'big' reachable until the end
}
