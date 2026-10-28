// Excerpt of L45 Json.kt, lines 22–35 (tested there; not a standalone file)
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
