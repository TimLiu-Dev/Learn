// A token bucket that counts whole tokens only.
class WholeTokenBucket(private val capacity: Long, private val perSecond: Long, private val now: () -> Long) {
    private var tokens = capacity
    private var last = now()

    fun tryAcquire(): Boolean {
        val t = now()
        tokens = minOf(capacity, tokens + (t - last) * perSecond / 1000)
        last = t
        if (tokens < 1) return false
        tokens--
        return true
    }
}

fun main() {
    var t = 0L
    val bucket = WholeTokenBucket(capacity = 2, perSecond = 2) { t }
    var granted = 0
    repeat(10) {                 // one request every 400 ms
        if (bucket.tryAcquire()) granted++
        t += 400
    }
    println("granted $granted of 10")
}
