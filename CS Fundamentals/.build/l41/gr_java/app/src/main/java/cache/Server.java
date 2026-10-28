package cache;

import cache.Catalog.Book;
import cache.Catalog.BookCatalog;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpServer;
import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.charset.StandardCharsets;
import java.util.concurrent.Executors;
import java.util.function.Supplier;

/** GET /books/{id}, PUT /books/{id} (body = new title), GET /stats. */
final class Server {
    private Server() {}

    static HttpServer start(BookCatalog catalog, Supplier<String> stats) throws IOException {
        var server = HttpServer.create(new InetSocketAddress("127.0.0.1", 0), 0);
        server.createContext("/books/", ex -> {
            Integer id = parseId(ex.getRequestURI().getPath().substring("/books/".length()));
            if (id == null) { send(ex, 400, "{\"error\":\"bad id\"}"); return; }
            switch (ex.getRequestMethod()) {
                case "GET" -> {
                    Book book = catalog.find(id);
                    if (book == null) send(ex, 404, "{\"error\":\"no book " + id + "\"}");
                    else send(ex, 200, "{\"id\":" + book.id() + ",\"title\":\"" + json(book.title()) + "\"}");
                }
                case "PUT" -> {
                    String title = new String(ex.getRequestBody().readAllBytes(), StandardCharsets.UTF_8).trim();
                    catalog.save(new Book(id, title));
                    send(ex, 204, null);
                }
                default -> send(ex, 405, "{\"error\":\"use GET or PUT\"}");
            }
        });
        server.createContext("/stats", ex -> send(ex, 200, stats.get()));
        server.setExecutor(Executors.newVirtualThreadPerTaskExecutor());      // L28
        server.start();
        return server;
    }

    private static Integer parseId(String s) {
        try { return Integer.parseInt(s); } catch (NumberFormatException e) { return null; }
    }

    private static void send(HttpExchange ex, int status, String body) throws IOException {
        if (body == null) { ex.sendResponseHeaders(status, -1); ex.close(); return; }
        byte[] bytes = body.getBytes(StandardCharsets.UTF_8);
        ex.getResponseHeaders().add("Content-Type", "application/json");
        ex.sendResponseHeaders(status, bytes.length);
        try (var out = ex.getResponseBody()) { out.write(bytes); }
    }

    private static String json(String s) { return s.replace("\\", "\\\\").replace("\"", "\\\""); }
}
