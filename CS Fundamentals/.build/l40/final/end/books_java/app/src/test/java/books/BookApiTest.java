package books;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

import books.Model.Book;
import books.Model.Page;
import books.Model.Problem;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpRequest.BodyPublishers;
import java.net.http.HttpResponse;
import java.net.http.HttpResponse.BodyHandlers;
import java.util.List;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import tools.jackson.databind.json.JsonMapper;

class BookApiTest {
    private BookApi api;
    private final HttpClient client = HttpClient.newHttpClient();
    private final JsonMapper json = JsonMapper.builder().build();

    @BeforeEach void start() throws Exception {
        api = new BookApi(0);                                  // port 0: the OS picks a free port
        api.start();
    }

    @AfterEach void stop() { api.stop(); }

    private HttpResponse<String> call(String method, String path, String body) throws Exception {
        var request = HttpRequest.newBuilder(URI.create("http://127.0.0.1:" + api.port() + path))
            .header("Content-Type", "application/json")
            .method(method, body == null ? BodyPublishers.noBody() : BodyPublishers.ofString(body))
            .build();
        return client.send(request, BodyHandlers.ofString());
    }

    private HttpResponse<String> create(String title, int year) throws Exception {
        return call("POST", "/books", "{\"title\":\"" + title + "\",\"year\":" + year + "}");
    }

    @Test @DisplayName("POST creates a book with 201 and a Location header")
    void post() throws Exception {
        var res = create("Dune", 1965);
        assertEquals(201, res.statusCode());
        assertEquals("/books/1", res.headers().firstValue("Location").orElse(null));
        assertEquals(new Book(1, "Dune", 1965), json.readValue(res.body(), Book.class));
    }

    @Test @DisplayName("GET returns the book, or a 404 problem")
    void get() throws Exception {
        create("Dune", 1965);
        assertEquals(200, call("GET", "/books/1", null).statusCode());
        var missing = call("GET", "/books/99", null);
        assertEquals(404, missing.statusCode());
        assertEquals("application/problem+json", missing.headers().firstValue("Content-Type").get());
        assertEquals("Not Found", json.readValue(missing.body(), Problem.class).title());
    }

    @Test @DisplayName("invalid input lists every error")
    void invalid() throws Exception {
        var res = call("POST", "/books", "{\"title\":\"  \",\"year\":3000}");
        assertEquals(400, res.statusCode());
        var problem = json.readValue(res.body(), Problem.class);
        assertEquals(List.of("title must not be blank", "year must be between 1440 and 2100"), problem.errors());
    }

    @Test @DisplayName("malformed JSON is a 400, not a 500")
    void malformed() throws Exception {
        assertEquals(400, call("POST", "/books", "{\"title\": \"Dune\", ").statusCode());
        assertEquals(400, call("POST", "/books", "{\"title\":\"Dune\",\"year\":\"old\"}").statusCode());
    }

    @Test @DisplayName("list pages through books with a next link")
    void paging() throws Exception {
        create("A", 2000); create("B", 2001); create("C", 2002);
        var first = json.readValue(call("GET", "/books?limit=2", null).body(), Page.class);
        assertEquals(List.of("A", "B"), first.items().stream().map(Book::title).toList());
        assertEquals("/books?limit=2&offset=2", first.next());
        var last = json.readValue(call("GET", first.next(), null).body(), Page.class);
        assertEquals(List.of("C"), last.items().stream().map(Book::title).toList());
        assertNull(last.next());
        assertEquals(400, call("GET", "/books?limit=0", null).statusCode());
    }

    @Test @DisplayName("PUT replaces and DELETE removes")
    void putDelete() throws Exception {
        create("Dune", 1965);
        var put = call("PUT", "/books/1", "{\"title\":\"Dune Messiah\",\"year\":1969}");
        assertEquals(200, put.statusCode());
        assertEquals("Dune Messiah", json.readValue(put.body(), Book.class).title());
        assertEquals(204, call("DELETE", "/books/1", null).statusCode());
        assertEquals(404, call("DELETE", "/books/1", null).statusCode());   // already gone
    }

    @Test @DisplayName("unsupported method is 405 with an Allow header")
    void notAllowed() throws Exception {
        var res = call("PATCH", "/books", null);
        assertEquals(405, res.statusCode());
        assertEquals("GET, POST", res.headers().firstValue("Allow").get());
    }

    @Test @DisplayName("GET reads from an injected repository")
    void injected() throws Exception {
        var repo = new InMemoryBookRepository();
        repo.add("Kindred", 1979);
        var other = new BookApi(0, repo);
        other.start();
        try {
            var res = client.send(HttpRequest.newBuilder(URI.create("http://127.0.0.1:" + other.port() + "/books/1"))
                                      .build(), BodyHandlers.ofString());
            assertEquals(new Book(1, "Kindred", 1979), json.readValue(res.body(), Book.class));
        } finally {
            other.stop();
        }
    }
}
