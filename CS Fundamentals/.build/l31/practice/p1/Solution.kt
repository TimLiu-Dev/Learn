data class Response(val status: Int, val headers: Map<String, String> = emptyMap(), val body: String = "")

class NotesApi {
    private val titles = mutableListOf("groceries")          // note 1

    fun handle(method: String, path: String, body: String = ""): Response {
        if (path == "/notes") return when (method) {
            "GET" -> Response(200, body = titles.joinToString("\n"))
            "POST" -> create(body.trim())
            else -> Response(405, mapOf("Allow" to "GET, POST"))   // the resource exists; the method doesn't
        }
        val n = path.removePrefix("/notes/").toIntOrNull()       // "/users" and "/notes/abc" give null
            ?: return Response(404, body = "no route for $path")
        if (method != "GET") return Response(405, mapOf("Allow" to "GET"))
        val title = titles.getOrNull(n - 1) ?: return Response(404, body = "no note $n")
        return Response(200, body = title)
    }

    private fun create(title: String): Response = when {
        title.isEmpty() -> Response(422, body = "a title is required")      // well-formed, but invalid
        title in titles -> Response(409, body = "'$title' already exists")  // clashes with current state
        else -> {
            titles += title
            Response(201, mapOf("Location" to "/notes/${titles.size}"))
        }
    }
}
