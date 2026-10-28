package shelf.http;

import com.sun.net.httpserver.HttpExchange;
import java.io.IOException;
import java.io.UncheckedIOException;
import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;

public final class Request {
    private final HttpExchange exchange;
    private final Map<String, String> params;
    private Map<String, String> query;                       // parsed on first use

    Request(HttpExchange exchange, Map<String, String> params) {
        this.exchange = exchange;
        this.params = params;
    }

    public String method() {
        return exchange.getRequestMethod();
    }

    public String path() {
        return exchange.getRequestURI().getPath();
    }

    public Map<String, String> params() {
        return params;
    }

    public String body() {
        try {
            return new String(exchange.getRequestBody().readAllBytes(), StandardCharsets.UTF_8);
        } catch (IOException e) {
            throw new UncheckedIOException(e);
        }
    }

    // "/books/abc" names no book, so a bad id is a 404, not a 400.
    public long id() {
        try {
            return Long.parseLong(params.get("id"));
        } catch (NumberFormatException e) {
            throw ApiError.notFound(path());
        }
    }

    public String query(String name) {
        if (query == null) query = parseQuery(exchange.getRequestURI().getRawQuery());
        return query.get(name);
    }

    public int intQuery(String name, int defaultValue, int min, int max) {
        String raw = query(name);
        if (raw == null) return defaultValue;
        try {
            int value = Integer.parseInt(raw);
            if (value >= min && value <= max) return value;
        } catch (NumberFormatException e) {
            // fall through to the error below
        }
        throw ApiError.invalid(java.util.List.of(name + " must be an integer in " + min + ".." + max + ", got '" + raw + "'"));
    }

    private static Map<String, String> parseQuery(String raw) {
        Map<String, String> out = new HashMap<>();
        if (raw == null) return out;
        for (String pair : raw.split("&")) {
            int eq = pair.indexOf('=');
            if (eq < 0) continue;
            out.put(URLDecoder.decode(pair.substring(0, eq), StandardCharsets.UTF_8),
                    URLDecoder.decode(pair.substring(eq + 1), StandardCharsets.UTF_8));
        }
        return out;
    }
}
