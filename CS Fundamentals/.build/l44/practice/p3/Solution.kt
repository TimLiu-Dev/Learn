data class Endpoint(val method: String, val path: String, val idempotencyKey: Boolean = false)

// RFC 9110 §9.2.2: PUT, DELETE and the safe methods are idempotent.
// A list of what's allowed means an unknown method is treated as not idempotent.
private val IDEMPOTENT = setOf("GET", "HEAD", "OPTIONS", "TRACE", "PUT", "DELETE")

fun isIdempotent(method: String): Boolean = method in IDEMPOTENT

// One review comment per endpoint a client can't safely retry after a timeout.
fun retryProblems(api: List<Endpoint>): List<String> =
    api.filter { !isIdempotent(it.method) && !it.idempotencyKey }
        .map { "${it.method} ${it.path} needs an Idempotency-Key" }
