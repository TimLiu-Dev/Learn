package shelf.http

import kotlinx.serialization.Serializable
import kotlinx.serialization.json.Json

// Null fields are left out of responses; unknown keys in requests are an error (strict by default).
val json = Json { explicitNulls = false }

class ApiError(
    val status: Int,
    val title: String,
    val detail: String,
    val errors: List<String>? = null,
) : RuntimeException(detail) {
    companion object {
        fun notFound(path: String) = ApiError(404, "Not Found", "No resource at $path")
        fun conflict(detail: String) = ApiError(409, "Conflict", detail)
        fun invalid(errors: List<String>) = ApiError(400, "Bad Request", "${errors.size} problem(s) with the request", errors)
    }
}

// RFC 9457 Problem Details (L32).
@Serializable
data class Problem(val type: String, val title: String, val status: Int, val detail: String, val errors: List<String>? = null)

inline fun <reified T> Request.bodyAs(): T = json.decodeFromString(body())

inline fun <reified T> ok(value: T, status: Int = 200, headers: Map<String, String> = emptyMap()) =
    Response(status, json.encodeToString(value), headers = headers)

fun noContent() = Response(204)

fun problem(e: ApiError, headers: Map<String, String> = emptyMap()) =
    Response(e.status, json.encodeToString(Problem("about:blank", e.title, e.status, e.detail, e.errors)),
             "application/problem+json", headers)
