data class Endpoint(val method: String, val path: String, val idempotencyKey: Boolean = false)

// Only POST is a problem.
fun isIdempotent(method: String): Boolean = method != "POST"

fun retryProblems(api: List<Endpoint>): List<String> =
    api.filter { !isIdempotent(it.method) && !it.idempotencyKey }
        .map { "${it.method} ${it.path} needs an Idempotency-Key" }
