// The same mailbox in Kotlin. Any has no wait()/notifyAll(), so the lock must be a java.lang.Object.
import kotlin.concurrent.thread

class Mailbox {
    @Suppress("PLATFORM_CLASS_MAPPED_TO_KOTLIN")
    private val lock = Object()
    private var letter: String? = null             // guarded by lock

    fun put(s: String) = synchronized(lock) {
        while (letter != null) lock.wait()
        letter = s
        lock.notifyAll()
    }

    fun take(): String = synchronized(lock) {
        while (letter == null) lock.wait()
        val s = letter!!
        letter = null
        lock.notifyAll()
        s
    }
}

fun main() {
    val box = Mailbox()
    val postman = thread { for (i in 1..3) box.put("letter $i") }
    repeat(3) { println("took ${box.take()}") }
    postman.join()
}
