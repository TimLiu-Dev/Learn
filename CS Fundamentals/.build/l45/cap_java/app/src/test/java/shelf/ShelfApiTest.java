package shelf;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.Clock;
import java.time.Instant;
import java.time.ZoneOffset;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

// Integration tests: the whole app on a real port, a real SQLite file, real HTTP.
class ShelfApiTest {
    private final Clock clock = Clock.fixed(Instant.parse("2026-10-27T10:00:00Z"), ZoneOffset.UTC);
    private final HttpClient client = HttpClient.newHttpClient();
    private Path dbFile;
    private Shelf shelf;

    @BeforeEach
    void start() throws Exception {
        dbFile = Files.createTempFile("shelf-api", ".db");
        shelf = new Shelf(dbFile, 0, clock).start();               // port 0: the OS picks a free port
    }

    @AfterEach
    void stop() throws Exception {
        shelf.close();
        Files.deleteIfExists(dbFile);
    }

    private HttpResponse<String> call(String method, String path, String body) throws Exception {
        var publisher = body == null ? HttpRequest.BodyPublishers.noBody() : HttpRequest.BodyPublishers.ofString(body);
        var request = HttpRequest.newBuilder(URI.create("http://127.0.0.1:" + shelf.port() + path))
            .method(method, publisher).header("Content-Type", "application/json").build();
        return client.send(request, HttpResponse.BodyHandlers.ofString());
    }

    private HttpResponse<String> call(String method, String path) throws Exception {
        return call(method, path, null);
    }

    private HttpResponse<String> addBook(String title, String author) throws Exception {
        return call("POST", "/books", "{\"title\":\"" + title + "\",\"author\":\"" + author + "\",\"year\":1965}");
    }

    private HttpResponse<String> addBook(String title) throws Exception {
        return addBook(title, "Frank Herbert");
    }

    @Test
    void healthIsUp() throws Exception {
        assertEquals("{\"status\":\"UP\"}", call("GET", "/health").body());
    }

    @Test
    void creatingABookReturns201ALocationAndTheBook() throws Exception {
        var r = addBook("Dune");
        assertEquals(201, r.statusCode());
        assertEquals("/books/1", r.headers().firstValue("Location").orElseThrow());
        assertEquals("{\"id\":1,\"title\":\"Dune\",\"author\":\"Frank Herbert\",\"year\":1965}", r.body());
        assertEquals(r.body(), call("GET", "/books/1").body());
    }

    @Test
    void invalidInputIsA400ProblemListingEveryError() throws Exception {
        var r = call("POST", "/books", "{\"title\":\" \",\"year\":3000}");
        assertEquals(400, r.statusCode());
        assertEquals("application/problem+json", r.headers().firstValue("Content-Type").orElseThrow());
        assertTrue(r.body().contains("title is required") && r.body().contains("author is required")
                   && r.body().contains("year must be"));
        assertEquals(400, call("POST", "/books", "{\"title\": oops").statusCode());
    }

    @Test
    void unknownThingsAre404AndWrongMethodsAre405WithAllow() throws Exception {
        assertEquals(404, call("GET", "/books/42").statusCode());
        assertEquals(404, call("GET", "/books/abc").statusCode());
        assertEquals(404, call("GET", "/authors").statusCode());
        var r = call("PATCH", "/books/1");
        assertEquals(405, r.statusCode());
        assertEquals("DELETE, GET, PUT", r.headers().firstValue("Allow").orElseThrow());
    }

    @Test
    void listsArePagedWithANextLink() throws Exception {
        for (int i = 0; i < 3; i++) addBook("Book " + i);
        var r = call("GET", "/books?limit=2");
        assertTrue(r.body().contains("\"total\":3") && r.body().contains("\"next\":\"/books?limit=2&offset=2\""), r.body());
        assertEquals(400, call("GET", "/books?limit=0").statusCode());
    }

    @Test
    void lendingReturningAndLendingAgain() throws Exception {
        addBook("Dune");
        call("POST", "/members", "{\"name\":\"Ada\",\"email\":\"ada@example.com\"}");
        var lent = call("POST", "/loans", "{\"bookId\":1,\"memberId\":1}");
        assertEquals(201, lent.statusCode());
        assertEquals("{\"id\":1,\"bookId\":1,\"memberId\":1,\"loaned\":\"2026-10-27\"}", lent.body());
        assertEquals(409, call("POST", "/loans", "{\"bookId\":1,\"memberId\":1}").statusCode());
        var returned = call("POST", "/loans/1/return");
        assertEquals("{\"id\":1,\"bookId\":1,\"memberId\":1,\"loaned\":\"2026-10-27\",\"returned\":\"2026-10-27\"}", returned.body());
        assertEquals(409, call("POST", "/loans/1/return").statusCode());
        assertEquals(201, call("POST", "/loans", "{\"bookId\":1,\"memberId\":1}").statusCode());
        assertTrue(call("GET", "/members/1/loans").body().startsWith("[{\"id\":1"));
    }

    @Test
    void theBusinessRulesComeBackAs404And409() throws Exception {
        for (int i = 0; i < 4; i++) addBook("Book " + i);
        call("POST", "/members", "{\"name\":\"Ada\",\"email\":\"ada@example.com\"}");
        assertEquals(409, call("POST", "/members", "{\"name\":\"Ada 2\",\"email\":\"ada@example.com\"}").statusCode());
        assertEquals(404, call("POST", "/loans", "{\"bookId\":9,\"memberId\":1}").statusCode());
        for (int b = 1; b <= 3; b++)
            assertEquals(201, call("POST", "/loans", "{\"bookId\":" + b + ",\"memberId\":1}").statusCode());
        var fourth = call("POST", "/loans", "{\"bookId\":4,\"memberId\":1}");
        assertEquals(409, fourth.statusCode());
        assertTrue(fourth.body().contains("already has 3 open loans"));
    }

    @Test
    void aLentBookCannotBeDeletedAFreshOneCan() throws Exception {
        addBook("Dune");
        addBook("Emma", "Jane Austen");
        call("POST", "/members", "{\"name\":\"Ada\",\"email\":\"ada@example.com\"}");
        call("POST", "/loans", "{\"bookId\":1,\"memberId\":1}");
        assertEquals(409, call("DELETE", "/books/1").statusCode());
        assertEquals(204, call("DELETE", "/books/2").statusCode());
        assertEquals(404, call("GET", "/books/2").statusCode());
    }
}
