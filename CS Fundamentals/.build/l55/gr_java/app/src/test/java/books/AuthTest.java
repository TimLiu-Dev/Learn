package books;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import books.Model.Book;
import books.Model.LoggedIn;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpRequest.BodyPublishers;
import java.net.http.HttpResponse;
import java.net.http.HttpResponse.BodyHandlers;
import java.util.List;
import java.util.Map;
import java.util.Set;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import tools.jackson.databind.json.JsonMapper;

class AuthTest {
    private long clock = 0;                                  // a fake clock the test can move
    private final Auth auth = new Auth(Map.of("ana", "correct horse", "ben", "battery staple", "root", "admin admin"),
            Set.of("root"), () -> clock);
    private BookApi api;
    private final HttpClient client = HttpClient.newHttpClient();   // no cookie jar: tests send Cookie by hand
    private final JsonMapper json = JsonMapper.builder().build();

    @BeforeEach void start() throws Exception { api = new BookApi(auth, 0); api.start(); }
    @AfterEach void stop() { api.stop(); }

    /** A logged-in user as a browser holds it: the cookie, plus the CSRF token the page keeps in memory. */
    record Who(String cookie, String csrf) {}

    private HttpResponse<String> call(String method, String path, String body, String cookie, String csrf) throws Exception {
        var request = HttpRequest.newBuilder(URI.create("http://127.0.0.1:" + api.port() + path))
                .header("Content-Type", "application/json")
                .method(method, body == null ? BodyPublishers.noBody() : BodyPublishers.ofString(body));
        if (cookie != null) request.header("Cookie", cookie);
        if (csrf != null) request.header("X-CSRF-Token", csrf);
        return client.send(request.build(), BodyHandlers.ofString());
    }
    private HttpResponse<String> call(String method, String path, String body, Who who) throws Exception {
        return call(method, path, body, who == null ? null : who.cookie(), who == null ? null : who.csrf());
    }

    private Who login(String name, String password) throws Exception {
        var res = call("POST", "/login", "{\"name\":\"" + name + "\",\"password\":\"" + password + "\"}", null);
        assertEquals(200, res.statusCode(), res.body());
        var setCookie = res.headers().firstValue("Set-Cookie").orElseThrow();
        return new Who(setCookie.substring(0, setCookie.indexOf(';')), json.readValue(res.body(), LoggedIn.class).csrfToken());
    }

    private HttpResponse<String> create(Who who) throws Exception {
        return call("POST", "/books", "{\"title\":\"Dune\",\"year\":1965}", who);
    }

    @Test @DisplayName("login sets a locked-down session cookie and returns a CSRF token")
    void loginCookie() throws Exception {
        var res = call("POST", "/login", "{\"name\":\"ana\",\"password\":\"correct horse\"}", null);
        assertEquals(200, res.statusCode());
        var cookie = res.headers().firstValue("Set-Cookie").orElseThrow();
        for (var attr : List.of("__Host-sid=", "Secure", "HttpOnly", "SameSite=Lax", "Path=/")) assertTrue(cookie.contains(attr), attr);
        assertEquals("ana", json.readValue(res.body(), LoggedIn.class).user());
    }

    @Test @DisplayName("wrong password and unknown user get identical answers")
    void sameFailure() throws Exception {
        var wrong = call("POST", "/login", "{\"name\":\"ana\",\"password\":\"nope\"}", null);
        var unknown = call("POST", "/login", "{\"name\":\"zed\",\"password\":\"nope\"}", null);
        assertEquals(401, wrong.statusCode());
        assertEquals(wrong.statusCode() + wrong.body(), unknown.statusCode() + unknown.body());
    }

    @Test @DisplayName("reading the catalogue needs no login")
    void publicRead() throws Exception {
        create(login("ana", "correct horse"));
        assertEquals(200, call("GET", "/books", null, null).statusCode());
        assertEquals(200, call("GET", "/books/1", null, null).statusCode());
    }

    @Test @DisplayName("writing without a session is 401 with a challenge")
    void noSession() throws Exception {
        var res = call("POST", "/books", "{\"title\":\"Dune\",\"year\":1965}", null);
        assertEquals(401, res.statusCode());
        assertTrue(res.headers().firstValue("WWW-Authenticate").isPresent());
    }

    @Test @DisplayName("a cookie without the CSRF header is 403")
    void noCsrf() throws Exception {
        var ana = login("ana", "correct horse");
        assertEquals(403, call("POST", "/books", "{\"title\":\"Dune\",\"year\":1965}", ana.cookie(), null).statusCode());
        assertEquals(403, call("POST", "/books", "{\"title\":\"Dune\",\"year\":1965}", ana.cookie(), "guess").statusCode());
    }

    @Test @DisplayName("the owner comes from the session, never from the request body")
    void ownerFromSession() throws Exception {
        var ana = login("ana", "correct horse");
        var res = create(ana);
        assertEquals(201, res.statusCode());
        assertEquals("ana", json.readValue(res.body(), Book.class).owner());
        var sneaky = call("POST", "/books", "{\"title\":\"Dune\",\"year\":1965,\"owner\":\"ben\"}", ana);
        assertEquals(400, sneaky.statusCode());                    // unknown field: rejected, not silently obeyed
    }

    @Test @DisplayName("another editor cannot change or delete your book")
    void notYours() throws Exception {
        create(login("ana", "correct horse"));
        var ben = login("ben", "battery staple");
        assertEquals(403, call("PUT", "/books/1", "{\"title\":\"Mine now\",\"year\":2000}", ben).statusCode());
        assertEquals(403, call("DELETE", "/books/1", null, ben).statusCode());
        assertEquals(200, call("GET", "/books/1", null, null).statusCode());   // still there
    }

    @Test @DisplayName("an admin may delete any book")
    void admin() throws Exception {
        create(login("ana", "correct horse"));
        assertEquals(204, call("DELETE", "/books/1", null, login("root", "admin admin")).statusCode());
        assertEquals(404, call("GET", "/books/1", null, null).statusCode());
    }

    @Test @DisplayName("logout kills the session on the server")
    void logout() throws Exception {
        var ana = login("ana", "correct horse");
        assertEquals(204, call("POST", "/logout", null, ana).statusCode());
        assertEquals(401, create(ana).statusCode());               // the old cookie is worthless now
    }

    @Test @DisplayName("each login gets a new id and an idle session expires")
    void rotationAndIdle() throws Exception {
        var first = login("ana", "correct horse");
        var second = login("ana", "correct horse");
        assertNotEquals(first.cookie(), second.cookie());          // no fixation: ids are never reused
        clock += 14 * 60_000;
        assertEquals(201, create(second).statusCode());            // 14 min idle: fine, and it resets the timer
        clock += 16 * 60_000;
        assertEquals(401, create(second).statusCode());            // 16 min idle: gone
    }
}
