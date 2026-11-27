package shelf

import java.net.URI
import java.net.http.HttpClient
import java.net.http.HttpRequest
import java.net.http.HttpResponse
import java.nio.file.Files
import java.time.Clock
import java.time.Instant
import java.time.ZoneOffset
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.JsonArray
import kotlinx.serialization.json.JsonElement
import kotlinx.serialization.json.JsonNull
import kotlinx.serialization.json.JsonObject
import kotlinx.serialization.json.JsonPrimitive
import kotlinx.serialization.json.jsonObject
import kotlin.test.AfterTest
import kotlin.test.Test
import kotlin.test.assertEquals

// A client's expectations, written down as data: only the fields the app's code really reads, and their
// JSON types. The app team owns these fixtures; Shelf's build runs them against the real server.
// Extra fields are fine (the app is a tolerant reader); a missing field or a changed type fails the build.
val BOOK_AS_THE_APP_READS_IT = mapOf("id" to "number", "title" to "string", "author" to "string")
val LOAN_AS_THE_APP_READS_IT = mapOf("id" to "number", "bookId" to "number", "loaned" to "string")
val PROBLEM_AS_THE_APP_READS_IT = mapOf("status" to "number", "title" to "string", "detail" to "string")

fun typeOf(e: JsonElement): String = when (e) {
    JsonNull -> "null"
    is JsonObject -> "object"
    is JsonArray -> "array"
    is JsonPrimitive -> if (e.isString) "string" else if (e.content == "true" || e.content == "false") "boolean" else "number"
}

fun violations(expected: Map<String, String>, body: JsonObject): List<String> =
    expected.mapNotNull { (field, type) ->
        val actual = body[field]
        when {
            actual == null -> "missing $field"
            typeOf(actual) != type -> "$field: expected $type, got ${typeOf(actual)}"
            else -> null
        }
    }

class ClientContractTest {
    private val dbFile = Files.createTempFile("shelf-client", ".db")
    private val clock = Clock.fixed(Instant.parse("2026-10-27T10:00:00Z"), ZoneOffset.UTC)
    private val shelf = Shelf(dbFile, 0, clock).start()
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

    private fun obj(r: HttpResponse<String>) = Json.parseToJsonElement(r.body()).jsonObject

    @Test
    fun `a book still has every field the app reads`() {
        call("POST", "/books", """{"title":"Dune","author":"Frank Herbert"}""")
        assertEquals(emptyList(), violations(BOOK_AS_THE_APP_READS_IT, obj(call("GET", "/books/1"))))
    }

    @Test
    fun `a loan still has every field the app reads`() {
        call("POST", "/books", """{"title":"Dune","author":"Frank Herbert"}""")
        call("POST", "/members", """{"name":"Ada","email":"ada@example.com"}""")
        assertEquals(emptyList(), violations(LOAN_AS_THE_APP_READS_IT, obj(call("POST", "/loans", """{"bookId":1,"memberId":1}"""))))
    }

    @Test
    fun `an error is still a problem the app can show`() {
        assertEquals(emptyList(), violations(PROBLEM_AS_THE_APP_READS_IT, obj(call("GET", "/books/99"))))
    }
}
