data class Response(val status: Int, val headers: Map<String, String> = emptyMap(), val body: String = "")

class NotesApi {
    private val titles = mutableListOf("groceries")          // note 1

    fun handle(method: String, path: String, body: String = ""): Response =
        TODO("route by path first, then by method")
}
