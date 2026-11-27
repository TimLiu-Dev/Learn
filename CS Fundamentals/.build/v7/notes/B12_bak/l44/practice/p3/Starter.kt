data class Endpoint(val method: String, val path: String, val idempotencyKey: Boolean = false)

// RFC 9110 §9.2.2: PUT, DELETE and the safe methods are idempotent.
fun isIdempotent(method: String): Boolean =
    TODO("true only for the six idempotent methods")

// One review comment per endpoint a client can't safely retry after a timeout.
fun retryProblems(api: List<Endpoint>): List<String> =
    TODO("keep endpoints that aren't idempotent and have no key, as \"METHOD path needs an Idempotency-Key\"")
