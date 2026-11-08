package books

import java.net.URI
import java.net.http.HttpClient
import java.net.http.HttpRequest
import java.net.http.HttpRequest.BodyPublishers
import java.net.http.HttpResponse
import java.net.http.HttpResponse.BodyHandlers
import kotlin.test.AfterTest
import kotlin.test.BeforeTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNotEquals
import kotlin.test.assertTrue
import kotlinx.serialization.json.Json

class AuthTest {
    private var clock = 0L                                   // a fake clock the test can move
    private val auth = Auth(mapOf("ana" to "correct horse", "ben" to "battery staple", "root" to "admin admin"),
        admins = setOf("root"), now = { clock })
    private val api = BookApi(auth, port = 0)
    private val client = HttpClient.newHttpClient()          // no cookie jar: tests send Cookie by hand
    private val json = Json { ignoreUnknownKeys = true }

    @BeforeTest fun start() = api.start()
    @AfterTest fun stop() = api.stop()

    /** A logged-in user as a browser holds it: the cookie, plus the CSRF token the page keeps in memory. */
    private data class Who(val cookie: String, val csrf: String)

    private fun call(method: String, path: String, body: String? = null, who: Who? = null, csrf: String? = who?.csrf): HttpResponse<String> {
        val request = HttpRequest.newBuilder(URI("http://127.0.0.1:${api.port}$path"))
            .header("Content-Type", "application/json")
            .method(method, body?.let { BodyPublishers.ofString(it) } ?: BodyPublishers.noBody())
        who?.let { request.header("Cookie", it.cookie) }
        csrf?.let { request.header("X-CSRF-Token", it) }
        return client.send(request.build(), BodyHandlers.ofString())
    }

    private fun login(name: String, password: String): Who {
        val res = call("POST", "/login", """{"name":"$name","password":"$password"}""")
        assertEquals(200, res.statusCode(), res.body())
        val cookie = res.headers().firstValue("Set-Cookie").get().substringBefore(";")
        return Who(cookie, json.decodeFromString<LoggedIn>(res.body()).csrfToken)
    }

    private fun create(who: Who, title: String = "Dune") = call("POST", "/books", """{"title":"$title","year":1965}""", who)

    @Test fun `login sets a locked-down session cookie and returns a CSRF token`() {
        val res = call("POST", "/login", """{"name":"ana","password":"correct horse"}""")
        assertEquals(200, res.statusCode())
        val cookie = res.headers().firstValue("Set-Cookie").get()
        for (attr in listOf("__Host-sid=", "Secure", "HttpOnly", "SameSite=Lax", "Path=/")) assertTrue(attr in cookie, attr)
        assertEquals("ana", json.decodeFromString<LoggedIn>(res.body()).user)
    }

    @Test fun `wrong password and unknown user get identical answers`() {
        val wrong = call("POST", "/login", """{"name":"ana","password":"nope"}""")
        val unknown = call("POST", "/login", """{"name":"zed","password":"nope"}""")
        assertEquals(401, wrong.statusCode())
        assertEquals(wrong.statusCode() to wrong.body(), unknown.statusCode() to unknown.body())
    }

    @Test fun `reading the catalogue needs no login`() {
        create(login("ana", "correct horse"))
        assertEquals(200, call("GET", "/books").statusCode())
        assertEquals(200, call("GET", "/books/1").statusCode())
    }

    @Test fun `writing without a session is 401 with a challenge`() {
        val res = call("POST", "/books", """{"title":"Dune","year":1965}""")
        assertEquals(401, res.statusCode())
        assertTrue(res.headers().firstValue("WWW-Authenticate").isPresent)
    }

    @Test fun `a cookie without the CSRF header is 403`() {
        val ana = login("ana", "correct horse")
        assertEquals(403, call("POST", "/books", """{"title":"Dune","year":1965}""", ana, csrf = null).statusCode())
        assertEquals(403, call("POST", "/books", """{"title":"Dune","year":1965}""", ana, csrf = "guess").statusCode())
    }

    @Test fun `the owner comes from the session, never from the request body`() {
        val ana = login("ana", "correct horse")
        val res = create(ana)
        assertEquals(201, res.statusCode())
        assertEquals("ana", json.decodeFromString<Book>(res.body()).owner)
        val sneaky = call("POST", "/books", """{"title":"Dune","year":1965,"owner":"ben"}""", ana)
        assertEquals(400, sneaky.statusCode())                       // unknown field: rejected, not silently obeyed
    }

    @Test fun `another editor cannot change or delete your book`() {
        create(login("ana", "correct horse"))
        val ben = login("ben", "battery staple")
        assertEquals(403, call("PUT", "/books/1", """{"title":"Mine now","year":2000}""", ben).statusCode())
        assertEquals(403, call("DELETE", "/books/1", who = ben).statusCode())
        assertEquals(200, call("GET", "/books/1").statusCode())     // still there
    }

    @Test fun `an admin may delete any book`() {
        create(login("ana", "correct horse"))
        assertEquals(204, call("DELETE", "/books/1", who = login("root", "admin admin")).statusCode())
        assertEquals(404, call("GET", "/books/1").statusCode())
    }

    @Test fun `logout kills the session on the server`() {
        val ana = login("ana", "correct horse")
        assertEquals(204, call("POST", "/logout", who = ana).statusCode())
        assertEquals(401, create(ana).statusCode())                 // the old cookie is worthless now
    }

    @Test fun `each login gets a new id and an idle session expires`() {
        val first = login("ana", "correct horse")
        val second = login("ana", "correct horse")
        assertNotEquals(first.cookie, second.cookie)                 // no fixation: ids are never reused
        clock += 14 * 60_000
        assertEquals(201, create(second).statusCode())               // 14 min idle: fine, and it resets the timer
        clock += 16 * 60_000
        assertEquals(401, create(second).statusCode())               // 16 min idle: gone
    }
}
