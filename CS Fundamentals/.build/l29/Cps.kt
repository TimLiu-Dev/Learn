import kotlinx.coroutines.*

suspend fun fetchUser(id: Int): String {
    delay(100)                  // suspension point 1
    val name = "user$id"
    delay(100)                  // suspension point 2
    return name.uppercase()
}

fun main() = runBlocking {
    println(fetchUser(7))
}
