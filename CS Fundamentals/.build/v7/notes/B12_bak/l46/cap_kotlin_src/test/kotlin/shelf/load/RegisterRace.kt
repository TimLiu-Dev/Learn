package shelf.load

import java.nio.file.Files
import java.time.Clock
import shelf.Shelf

// Exercise 1: 10 sign-ups with the SAME email, all at once.
fun main() {
    val dbFile = Files.createTempFile("shelf-register", ".db")
    Shelf(dbFile, 0, Clock.systemUTC()).start().use { shelf ->
        val api = Api(shelf.port)
        repeat(3) { round ->
            val email = "ada$round@example.com"
            val codes = api.burst(10) { api.post("/members", """{"name":"Ada","email":"$email"}""") }
            println("round ${round + 1}: ${codes.tally()}")
        }
    }
    Files.deleteIfExists(dbFile)
}
