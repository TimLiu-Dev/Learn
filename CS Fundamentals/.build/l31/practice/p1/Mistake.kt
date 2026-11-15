data class Response(val status: Int, val headers: Map<String, String> = emptyMap(), val body: String = "")

class NotesApi {
    private val titles = mutableListOf("groceries")

    // Matches method and path together, so a wrong method falls through to 404.
    fun handle(method: String, path: String, body: String = ""): Response {
        val n = path.removePrefix("/notes/").toIntOrNull()
        return when {
            method == "GET" && path == "/notes" -> Response(200, body = titles.joinToString("\n"))
            method == "POST" && path == "/notes" -> when {
                body.isBlank() -> Response(422)
                body in titles -> Response(409)
                else -> { titles += body; Response(201, mapOf("Location" to "/notes/${titles.size}")) }
            }
            method == "GET" && n != null && n in 1..titles.size -> Response(200, body = titles[n - 1])
            else -> Response(404)
        }
    }
}
