import java.nio.ByteBuffer
import java.util.concurrent.CountDownLatch
import kotlin.concurrent.thread

// Fills three memory areas outside the heap, then waits so jcmd can look.
data class Point(val x: Int, val y: Int)

fun main(args: Array<String>) {
    val points = Array(1_000_000) { Point(it, -it) }          // heap: 1M small objects

    val direct = ByteBuffer.allocateDirect(64 shl 20)         // 64 MB off-heap
    for (i in 0 until direct.capacity() step 4096) direct.put(i, 1)

    val never = CountDownLatch(1)
    repeat(50) {                                              // 50 idle threads
        thread(isDaemon = true) { never.await() }
    }
    println("pid ${ProcessHandle.current().pid()}, ${points.size} points, " +
            "${direct.capacity() shr 20} MB direct")
    Thread.sleep(args.firstOrNull()?.toLong() ?: 5000)
}
