fun main() {
    val q = MyQueue<String>(4)
    q.offer("A")
    q.offer("B")
    q.offer("C")
    println(q.poll())               // A: first in, first out
    println(q.poll())               // B
    q.offer("D")
    q.offer("E")                    // tail wraps around to slot 0
    q.offer("F")                    // slot 1: the buffer is full again
    q.offer("G")                    // full: grow to 8, copying in queue order
    println(q.debug())
    while (q.size > 0) print("${q.poll()} ")
    println()
}
