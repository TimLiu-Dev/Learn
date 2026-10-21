enum class Outcome { CREATED, FOUND, UPDATED, DELETED, NOT_FOUND, INVALID, CONFLICT }

// RFC 9110 §9.2: GET, HEAD, OPTIONS, TRACE are safe; those plus PUT and DELETE are idempotent
fun isSafe(method: String) = method in setOf("GET", "HEAD", "OPTIONS", "TRACE")
fun isIdempotent(method: String) = isSafe(method) || method in setOf("PUT", "DELETE")

fun status(outcome: Outcome): Int = when (outcome) {
    Outcome.CREATED -> 201
    Outcome.FOUND, Outcome.UPDATED -> 200
    Outcome.DELETED -> 204
    Outcome.NOT_FOUND -> 404
    Outcome.INVALID -> 400
    Outcome.CONFLICT -> 409
}

fun main() {
    val requests = listOf(
        "POST /books" to Outcome.CREATED,
        "GET /books/7" to Outcome.FOUND,
        "GET /books/99" to Outcome.NOT_FOUND,
        "PUT /books/7" to Outcome.UPDATED,
        "POST /books" to Outcome.INVALID,
        "POST /loans" to Outcome.CONFLICT,
        "DELETE /books/7" to Outcome.DELETED,
        "PATCH /books/7" to Outcome.UPDATED,
    )
    for ((req, outcome) in requests) {
        val method = req.substringBefore(' ')
        println("%-16s -> %d  safe=%-5s auto-retry=%s".format(req, status(outcome), isSafe(method), isIdempotent(method)))
    }
}
