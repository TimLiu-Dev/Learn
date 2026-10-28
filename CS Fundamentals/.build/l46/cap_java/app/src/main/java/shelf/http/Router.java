package shelf.http;

import com.sun.net.httpserver.HttpExchange;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Consumer;
import tools.jackson.core.JacksonException;

public final class Router {
    @FunctionalInterface
    public interface Handler {
        Response handle(Request request);
    }

    public sealed interface Match {
        record Found(Handler handler, Map<String, String> params) implements Match {}
        record WrongMethod(List<String> allowed) implements Match {}
        record NoRoute() implements Match {}
    }

    private record Route(String method, List<String> segments, Handler handler) {}

    private final List<Route> routes = new ArrayList<>();
    private final Consumer<String> log;

    public Router() {
        this(System.err::println);
    }

    public Router(Consumer<String> log) {
        this.log = log;
    }

    public void get(String path, Handler h) { add("GET", path, h); }
    public void post(String path, Handler h) { add("POST", path, h); }
    public void put(String path, Handler h) { add("PUT", path, h); }
    public void delete(String path, Handler h) { add("DELETE", path, h); }

    private void add(String method, String path, Handler handler) {
        routes.add(new Route(method, segments(path), handler));
    }

    // Pure function of (method, path), so it's easy to unit-test without a server.
    public Match match(String method, String path) {
        List<String> parts = segments(path);
        List<String> allowed = new ArrayList<>();
        for (Route route : routes) {
            Map<String, String> params = bind(route.segments(), parts);
            if (params == null) continue;
            if (route.method().equals(method)) return new Match.Found(route.handler(), params);
            if (!allowed.contains(route.method())) allowed.add(route.method());
        }
        if (allowed.isEmpty()) return new Match.NoRoute();
        allowed.sort(null);
        return new Match.WrongMethod(allowed);
    }

    // "/books/{id}" against "/books/7" gives {id=7}; null if the shapes differ.
    private static Map<String, String> bind(List<String> template, List<String> parts) {
        if (template.size() != parts.size()) return null;
        Map<String, String> params = new HashMap<>();
        for (int i = 0; i < template.size(); i++) {
            String t = template.get(i);
            String p = parts.get(i);
            if (t.startsWith("{") && t.endsWith("}")) params.put(t.substring(1, t.length() - 1), p);
            else if (!t.equals(p)) return null;
        }
        return params;
    }

    private static List<String> segments(String path) {
        return Arrays.stream(path.split("/")).filter(s -> !s.isEmpty()).toList();
    }

    // HttpServer calls this on a virtual thread for every request.
    public void handle(HttpExchange exchange) throws IOException {
        try (exchange) {
            String path = exchange.getRequestURI().getPath();
            Response response;
            try {
                response = switch (match(exchange.getRequestMethod(), path)) {
                    case Match.Found(Handler handler, Map<String, String> params) ->
                        handler.handle(new Request(exchange, params));
                    case Match.WrongMethod(List<String> allowed) -> Json.problem(   // RFC 9110: a 405 MUST send Allow
                        new ApiError(405, "Method Not Allowed", exchange.getRequestMethod() + " is not supported on " + path),
                        Map.of("Allow", String.join(", ", allowed)));
                    case Match.NoRoute() -> Json.problem(ApiError.notFound(path), Map.of());
                };
            } catch (ApiError e) {
                response = Json.problem(e, Map.of());
            } catch (JacksonException e) {                       // malformed JSON or wrong types
                response = Json.problem(new ApiError(400, "Bad Request", e.getOriginalMessage()), Map.of());
            } catch (RuntimeException e) {                       // log the details, never send them (L33)
                Map<String, Object> line = new LinkedHashMap<>();
                line.put("level", "ERROR");
                line.put("requestId", exchange.getResponseHeaders().getFirst("X-Request-Id"));
                line.put("method", exchange.getRequestMethod());
                line.put("path", path);
                line.put("error", e.toString());
                log.accept(AccessLogFilter.jsonLine(line));
                response = Json.problem(new ApiError(500, "Internal Server Error", "Something went wrong"), Map.of());
            }
            send(exchange, response);
        }
    }

    // Writes a Response to the exchange. Package-visible so filters (rate limiter) can answer too.
    static void send(HttpExchange exchange, Response response) throws IOException {
        response.headers().forEach((name, value) -> exchange.getResponseHeaders().set(name, value));
        if (response.body() == null) {
            exchange.sendResponseHeaders(response.status(), -1);  // -1: no body at all (204)
            return;
        }
        byte[] bytes = response.body().getBytes(StandardCharsets.UTF_8);
        exchange.getResponseHeaders().set("Content-Type", response.type());
        exchange.sendResponseHeaders(response.status(), bytes.length);
        exchange.getResponseBody().write(bytes);
    }
}
