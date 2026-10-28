// Excerpt of L45 ShelfApiTest.kt, lines 16–34 (tested there; not a standalone file)
// Integration tests: the whole app on a real port, a real SQLite file, real HTTP.
class ShelfApiTest {
    private val dbFile = Files.createTempFile("shelf-api", ".db")
    private val clock = Clock.fixed(Instant.parse("2026-10-27T10:00:00Z"), ZoneOffset.UTC)
    private val shelf = Shelf(dbFile, 0, clock).start()                 // port 0: the OS picks a free port
    private val client = HttpClient.newHttpClient()

    @AfterTest
    fun tearDown() {
        shelf.close()
        Files.deleteIfExists(dbFile)
    }

    private fun call(method: String, path: String, body: String? = null): HttpResponse<String> {
        val publisher = body?.let { HttpRequest.BodyPublishers.ofString(it) } ?: HttpRequest.BodyPublishers.noBody()
        val request = HttpRequest.newBuilder(URI("http://127.0.0.1:${shelf.port}$path"))
            .method(method, publisher).header("Content-Type", "application/json").build()
        return client.send(request, HttpResponse.BodyHandlers.ofString())
    }
