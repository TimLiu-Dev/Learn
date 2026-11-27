import kotlinx.coroutines.*
import kotlinx.coroutines.channels.*

fun main() = runBlocking {
    val ch = Channel<Int>(capacity = 3)
    ch.send(1)
    ch.send(2)
    ch.close()
    val got = mutableListOf<Int>()
    for (x in ch) got += x
    println("$got ${ch.receiveCatching().isClosed}")
}
