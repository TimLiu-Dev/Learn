fun main() {
    val rt = Runtime.getRuntime()
    println("max heap:   ${rt.maxMemory() / 1024 / 1024} MB")
    println("processors: ${rt.availableProcessors()}")
}
