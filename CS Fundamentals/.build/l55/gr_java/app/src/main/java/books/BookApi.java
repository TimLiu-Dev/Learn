package books;

import books.Model.Book;
import books.Model.LoggedIn;
import books.Model.Login;
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
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicLong;
import java.util.function.IntPredicate;
import tools.jackson.core.JacksonException;
import tools.jackson.databind.DeserializationFeature;
import tools.jackson.databind.json.JsonMapper;

public class BookApi {
    // Jackson 3 ignores unknown fields by default (L32). For a body that might try to set "owner", reject instead.
    private final JsonMapper json = JsonMapper.builder().enable(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES).build();
    private final Map<Long, Book> books = new ConcurrentHashMap<>();             // L27: safe for concurrent handlers
    private final AtomicLong nextId = new AtomicLong(1);
    private final ExecutorService pool = Executors.newVirtualThreadPerTaskExecutor();  // L28: one per request
    private final HttpServer server;
    private final Auth auth;

    interface Handler { void handle(HttpExchange ex) throws IOException; }

    public BookApi(Auth auth, int port) throws IOException {
        this.auth = auth;
        server = HttpServer.create(new InetSocketAddress(InetAddress.getLoopbackAddress(), port), 0);
        server.createContext("/books", exchange -> { try (exchange) { handle(exchange, this::route); } });
        server.createContext("/login", exchange -> { try (exchange) { handle(exchange, this::login); } });
        server.createContext("/logout", exchange -> { try (exchange) { handle(exchange, this::logout); } });
        server.setExecutor(pool);
    }

    public int port() { return server.getAddress().getPort(); }
    public void start() { server.start(); }
    public void stop() {
        server.stop(0);
        pool.close();
    }

    // ---- the "framework": catch-all error handling ----

    private void handle(HttpExchange ex, Handler handler) throws IOException {
        try {
            handler.handle(ex);
        } catch (JacksonException e) {                                   // malformed JSON, wrong types, unknown fields
            problem(ex, 400, "Bad Request", e.getOriginalMessage(), null);
        } catch (Exception e) {
            System.err.println("500 for " + ex.getRequestURI() + ": " + e);   // details to the log, not the client (L33)
            problem(ex, 500, "Internal Server Error", "something went wrong", null);
        }
    }

    // ---- authentication ----

    private static String sessionId(HttpExchange ex) {
        String header = ex.getRequestHeaders().getFirst("Cookie");
        if (header == null) return null;
        for (String c : header.split(";"))
            if (c.trim().startsWith(Auth.COOKIE + "=")) return c.trim().substring(Auth.COOKIE.length() + 1);
        return null;
    }

    private void login(HttpExchange ex) throws IOException {
        if (!ex.getRequestMethod().equals("POST")) { notAllowed(ex, "POST"); return; }
        Login input = json.readValue(ex.getRequestBody().readAllBytes(), Login.class);
        var result = auth.login(input.name(), input.password());
        if (result == null) { problem(ex, 401, "Unauthorized", "invalid name or password", null); return; }   // same words either way
        ex.getResponseHeaders().add("Set-Cookie", Auth.cookie(result.id()));
        send(ex, 200, json.writeValueAsString(new LoggedIn(result.session().user, result.session().csrf)), "application/json");
    }

    private void logout(HttpExchange ex) throws IOException {
        if (!ex.getRequestMethod().equals("POST")) { notAllowed(ex, "POST"); return; }
        auth.logout(sessionId(ex));                                      // forget it on the server
        ex.getResponseHeaders().add("Set-Cookie", Auth.CLEAR);           // and ask the browser to drop it
        send(ex, 204, null, null);
    }

    /** The three gates every unsafe request passes, in order. Returns the session, or null after replying. */
    private Auth.Session guard(HttpExchange ex) throws IOException {
        var s = auth.session(sessionId(ex));
        if (s == null) {
            // RFC 9110 §15.5.2: a 401 MUST carry WWW-Authenticate. No registered scheme fits cookies, so we name ours.
            ex.getResponseHeaders().set("WWW-Authenticate", "Session realm=\"books\"");
            problem(ex, 401, "Unauthorized", "log in first: POST /login", null);
            return null;
        }
        if (!auth.csrfOk(s, ex.getRequestHeaders().getFirst("X-CSRF-Token"))) {
            problem(ex, 403, "Forbidden", "missing or wrong X-CSRF-Token", null);
            return null;
        }
        return s;
    }

    private static boolean mayChange(Auth.Session s, Book book) { return s.admin || book.owner().equals(s.user); }   // ownership (A01)

    // ---- routing by hand, as in L32 ----

    private void route(HttpExchange ex) throws IOException {
        String[] parts = ex.getRequestURI().getPath().replaceAll("^/+|/+$", "").split("/");
        if (!parts[0].equals("books")) { notFound(ex); return; }
        String method = ex.getRequestMethod();
        if (parts.length == 1) {
            switch (method) {
                case "GET" -> list(ex);                                  // the catalogue is public
                case "POST" -> { var s = guard(ex); if (s != null) create(ex, s); }
                default -> notAllowed(ex, "GET, POST");
            }
        } else if (parts.length == 2) {
            Long id = parseId(parts[1]);
            if (id == null) { notFound(ex); return; }
            switch (method) {
                case "GET" -> {
                    Book b = books.get(id);
                    if (b != null) send(ex, 200, json.writeValueAsString(b), "application/json"); else notFound(ex);
                }
                case "PUT", "DELETE" -> {
                    var s = guard(ex);
                    if (s == null) return;
                    Book book = books.get(id);
                    if (book == null) { notFound(ex); return; }
                    if (!mayChange(s, book)) { problem(ex, 403, "Forbidden", "only the owner or an admin may change this book", null); return; }
                    if (method.equals("PUT")) replace(ex, book);
                    else { books.remove(id); send(ex, 204, null, null); }
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

    private void list(HttpExchange ex) throws IOException {                     // unchanged from L32
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
        if (!errors.isEmpty()) { problem(ex, 400, "Invalid query", errors.size() + " problem(s) with the request", errors); return; }

        List<Book> all = books.values().stream().sorted(Comparator.comparingLong(Book::id)).toList();
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

    private void create(HttpExchange ex, Auth.Session s) throws IOException {
        NewBook input = json.readValue(ex.getRequestBody().readAllBytes(), NewBook.class);
        List<String> errors = Model.validate(input);
        if (!errors.isEmpty()) { problem(ex, 400, "Invalid book", errors.size() + " problem(s)", errors); return; }
        Book book = new Book(nextId.getAndIncrement(), input.title().trim(), input.year(), s.user);   // owner from the session, never the body
        books.put(book.id(), book);
        ex.getResponseHeaders().set("Location", "/books/" + book.id());
        send(ex, 201, json.writeValueAsString(book), "application/json");
    }

    private void replace(HttpExchange ex, Book old) throws IOException {
        NewBook input = json.readValue(ex.getRequestBody().readAllBytes(), NewBook.class);
        List<String> errors = Model.validate(input);
        if (!errors.isEmpty()) { problem(ex, 400, "Invalid book", errors.size() + " problem(s)", errors); return; }
        Book book = new Book(old.id(), input.title().trim(), input.year(), old.owner());   // the owner can't be changed here
        books.put(book.id(), book);
        send(ex, 200, json.writeValueAsString(book), "application/json");
    }

    // ---- responses ----

    private void notFound(HttpExchange ex) throws IOException {
        problem(ex, 404, "Not Found", "No resource at " + ex.getRequestURI().getPath(), null);
    }

    private void notAllowed(HttpExchange ex, String allow) throws IOException {
        ex.getResponseHeaders().set("Allow", allow);
        problem(ex, 405, "Method Not Allowed", ex.getRequestMethod() + " is not supported here", null);
    }

    private void problem(HttpExchange ex, int status, String title, String detail, List<String> errors) throws IOException {
        send(ex, status, json.writeValueAsString(new Problem("about:blank", title, status, detail, errors)), "application/problem+json");
    }

    private void send(HttpExchange ex, int status, String body, String type) throws IOException {
        if (body == null) {
            ex.sendResponseHeaders(status, -1);
            return;
        }
        byte[] bytes = body.getBytes(StandardCharsets.UTF_8);
        ex.getResponseHeaders().set("Content-Type", type);
        ex.sendResponseHeaders(status, bytes.length);
        try (OutputStream out = ex.getResponseBody()) { out.write(bytes); }
    }
}
