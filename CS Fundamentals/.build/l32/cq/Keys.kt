import java.util.UUID

val firstResult = HashMap<String, Int>()   // server: Idempotency-Key -> order id
var created = 0

fun postOrder(key: String): Int = firstResult.getOrPut(key) { ++created }

fun main() {
    // Client A makes its key once, then tries three times
    val key = UUID.randomUUID().toString()
    repeat(3) { postOrder(key) }

    // Client B makes a fresh key on every try
    repeat(3) { postOrder(UUID.randomUUID().toString()) }

    println(created)
}
