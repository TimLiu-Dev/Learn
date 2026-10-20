fun risky(): Int {
    try {
        throw IllegalStateException("important failure")
    } finally {
        return -1          // DON'T: this silently discards the exception
    }
}

fun main() {
    println(risky())
    println("no one ever hears about the failure")
}
