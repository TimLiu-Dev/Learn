package books;

import books.Model.Book;
import books.Model.NewBook;
import books.Model.Page;
import books.Model.Problem;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpServer;
import java.io.IOException;
import java.io.OutputStream;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.function.IntPredicate;
import tools.jackson.core.JacksonException;
import tools.jackson.databind.json.JsonMapper;

public class BookApi {
    private final JsonMapper json = JsonMapper.builder().build();
    private final BookRepository repo;
    private final ExecutorService pool = Executors.newVirtualThreadPerTaskExecutor();  // L28: one per request
    private final HttpServer server;

    public BookApi(int port) throws IOException {
        this(port, new InMemoryBookRepository());
    }

    public BookApi(int port, BookRepository repo) throws IOException {
        this.repo = repo;
        server = HttpServer.create(new InetSocketAddress(InetAddress.getLoopbackAddress(), port), 0);
        server.createContext("/books", exchange -> {
            try (exchange) { handle(exchange); }
        });
        server.setExecutor(pool);
    }

    public int port() { return server.getAddress().getPort(); }
    public void start() { server.start(); }
    public void stop() {
        server.stop(0);
        pool.close();
    }

    // ---- the "framework": catch-all error handling + routing by hand ----

    private void handle(HttpExchange ex) throws IOException {
        try {
            route(ex);
        } catch (JacksonException e) {                                   // malformed JSON or wrong types
            problem(ex, 400, "about:blank", "Bad Request", e.getOriginalMessage(), null);
        } catch (Exception e) {
            problem(ex, 500, "about:blank", "Internal Server Error", e.toString(), null);
        }
    }

    private void route(HttpExchange ex) throws IOException {
        String[] parts = ex.getRequestURI().getPath().replaceAll("^/+|/+$", "").split("/");  // [books, 7]
        if (!parts[0].equals("books")) { notFound(ex); return; }          // the context also matches "/booksX"
        String method = ex.getRequestMethod();
        if (parts.length == 1) {
            switch (method) {
                case "GET" -> list(ex);
                case "POST" -> create(ex);
                default -> notAllowed(ex, "GET, POST");
            }
        } else if (parts.length == 2) {
            Long id = parseId(parts[1]);
            if (id == null) { notFound(ex); return; }
            switch (method) {
                case "GET" -> {
                    Optional<Book> b = repo.find(id);
                    if (b.isPresent()) send(ex, 200, json.writeValueAsString(b.get()), "application/json"); else notFound(ex);
                }
                case "PUT" -> replace(ex, id);
                case "DELETE" -> {
                    if (repo.delete(id)) send(ex, 204, null, null); else notFound(ex);
                }
                default -> notAllowed(ex, "GET, PUT, DELETE");
            }
        } else {
            notFound(ex);
        }
    }

    private static Long parseId(String s) {
        try { return Long.parseLong(s); } catch (NumberFormatException e) { return null; }
    }

    // ---- handlers ----

    private void list(HttpExchange ex) throws IOException {
        Map<String, String> q = new HashMap<>();
        String query = ex.getRequestURI().getQuery();
        if (query != null) {
            for (String pair : query.split("&")) {
                int eq = pair.indexOf('=');
                if (eq > 0) q.put(pair.substring(0, eq), pair.substring(eq + 1));
            }
        }
        List<String> errors = new ArrayList<>();
        int limit = intParam(q, "limit", 10, "1..100", n -> n >= 1 && n <= 100, errors);
        int offset = intParam(q, "offset", 0, ">= 0", n -> n >= 0, errors);
        if (!errors.isEmpty()) { invalid(ex, "Invalid query", errors); return; }

        List<Book> all = repo.all();
        String next = offset + limit < all.size() ? "/books?limit=" + limit + "&offset=" + (offset + limit) : null;
        List<Book> items = all.stream().skip(offset).limit(limit).toList();
        send(ex, 200, json.writeValueAsString(new Page(items, limit, offset, all.size(), next)), "application/json");
    }

    private static int intParam(Map<String, String> q, String name, int dflt, String rule,
                                IntPredicate ok, List<String> errors) {
        String raw = q.get(name);
        if (raw == null) return dflt;
        try {
            int n = Integer.parseInt(raw);
            if (ok.test(n)) return n;
        } catch (NumberFormatException ignored) { }
        errors.add(name + " must be " + rule + ", got '" + raw + "'");
        return dflt;
    }

    private void create(HttpExchange ex) throws IOException {
        NewBook input = json.readValue(ex.getRequestBody().readAllBytes(), NewBook.class);
        List<String> errors = Model.validate(input);
        if (!errors.isEmpty()) { invalid(ex, "Invalid book", errors); return; }
        Book book = repo.add(input.title().trim(), input.year());
        System.out.println("DEBUG repo now has " + repo.all().size() + " books");
        ex.getResponseHeaders().set("Location", "/books/" + book.id());
        send(ex, 201, json.writeValueAsString(book), "application/json");
    }

    private void replace(HttpExchange ex, long id) throws IOException {
        if (repo.find(id).isEmpty()) { notFound(ex); return; }
        NewBook input = json.readValue(ex.getRequestBody().readAllBytes(), NewBook.class);
        List<String> errors = Model.validate(input);
        if (!errors.isEmpty()) { invalid(ex, "Invalid book", errors); return; }
        Book book = new Book(id, input.title().trim(), input.year());
        repo.replace(book);
        send(ex, 200, json.writeValueAsString(book), "application/json");
    }

    // ---- responses ----

    private void notFound(HttpExchange ex) throws IOException {
        problem(ex, 404, "about:blank", "Not Found", "No resource at " + ex.getRequestURI().getPath(), null);
    }

    private void notAllowed(HttpExchange ex, String allow) throws IOException {
        ex.getResponseHeaders().set("Allow", allow);                     // RFC 9110: a 405 MUST list them
        problem(ex, 405, "about:blank", "Method Not Allowed", ex.getRequestMethod() + " is not supported here", null);
    }

    private void invalid(HttpExchange ex, String title, List<String> errors) throws IOException {
        problem(ex, 400, "https://example.com/problems/validation", title,
                errors.size() + " problem(s) with the request", errors);
    }

    private void problem(HttpExchange ex, int status, String type, String title, String detail,
                         List<String> errors) throws IOException {
        String body = json.writeValueAsString(new Problem(type, title, status, detail, errors));
        send(ex, status, body, "application/problem+json");
    }

    private void send(HttpExchange ex, int status, String body, String type) throws IOException {
        System.out.println(ex.getRequestMethod() + " " + ex.getRequestURI() + " -> " + status);
        if (body == null) {
            ex.sendResponseHeaders(status, -1);                              // -1: no body at all
            return;
        }
        byte[] bytes = body.getBytes(StandardCharsets.UTF_8);
        ex.getResponseHeaders().set("Content-Type", type);
        ex.sendResponseHeaders(status, bytes.length);
        try (OutputStream out = ex.getResponseBody()) { out.write(bytes); }
    }
}
