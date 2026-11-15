import java.util.*;
import java.util.function.Function;

public class Practice {
    record Response(int status, String body, String allow) { }

    record Route(String method, String pattern, Function<Map<String, String>, Response> handler) { }

    static class Router {
        private final List<Route> routes = new ArrayList<>();

        void add(String method, String pattern, Function<Map<String, String>, Response> handler) {
            routes.add(new Route(method, pattern, handler));
        }

        Response handle(String method, String path) {
            for (Route r : routes) {
                Map<String, String> params = match(r.pattern(), path);
                if (params != null && r.method().equals(method)) return r.handler().apply(params);
            }
            return new Response(404, "no route for " + path, null);   // TODO: 405 when the path exists
        }
    }

    // "/books/{id}" + "/books/42" -> {id=42}; "/books" + "/books" -> {}; no fit -> null
    static Map<String, String> match(String pattern, String path) {
        throw new UnsupportedOperationException("TODO: split both on '/' and compare segments");
    }

    // CHECKS
    static Router bookRouter() {
        Router r = new Router();
        r.add("GET", "/books", p -> new Response(200, "all books", null));
        r.add("POST", "/books", p -> new Response(201, "created", null));
        r.add("GET", "/books/{id}", p -> new Response(200, "book " + p.get("id"), null));
        r.add("PUT", "/books/{id}", p -> new Response(200, "replaced " + p.get("id"), null));
        r.add("GET", "/books/{id}/loans/{loan}", p -> new Response(200, "loan " + p.get("loan") + " of book " + p.get("id"), null));
        return r;
    }

    public static void main(String[] args) {
        check("match captures path parameters", "{id=42} {} null",
                () -> match("/books/{id}", "/books/42") + " " + match("/books", "/books") + " " + match("/books", "/authors"));
        check("a request reaches its handler with its parameters", "all books | book 42 | loan 3 of book 7", () -> {
            Router r = bookRouter();
            return r.handle("GET", "/books").body() + " | " + r.handle("GET", "/books/42").body() + " | " + r.handle("GET", "/books/7/loans/3").body();
        });
        check("an unknown path is 404", 404, () -> bookRouter().handle("GET", "/authors").status());
        check("prefixes and extra segments are not matches", "404 404 404", () -> {
            Router r = bookRouter();
            return r.handle("GET", "/booksXYZ").status() + " " + r.handle("GET", "/books/42/extra").status() + " " + r.handle("GET", "/books//loans/3").status();
        });
        check("a known path with the wrong method is 405 with Allow", "405 GET, POST", () -> {
            Response res = bookRouter().handle("PATCH", "/books");
            return res.status() + " " + res.allow();
        });
        check("Allow lists only the methods of the matching path", "GET, PUT", () -> bookRouter().handle("DELETE", "/books/7").allow());
    }

    interface Check { Object get() throws Exception; }

    static void check(String name, Object expected, Check actual) {
        try {
            Object got = actual.get();
            System.out.println(Objects.equals(expected, got) ? "PASS " + name : "FAIL " + name + ": expected <" + expected + "> but was <" + got + ">");
        } catch (Exception e) {
            System.out.println("FAIL " + name + ": threw " + e);
        }
    }
    // END CHECKS
}
