import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

public class Practice {
    record Response(int status, String body, String allow) { }

    record Route(String method, String pattern, Function<Map<String, String>, Response> handler) { }

    static class Router {
        private final List<Route> routes = new ArrayList<>();

        void add(String method, String pattern, Function<Map<String, String>, Response> handler) {
            routes.add(new Route(method, pattern, handler));
        }

        Response handle(String method, String path) {
            List<Route> fits = routes.stream().filter(r -> match(r.pattern(), path) != null).toList();
            if (fits.isEmpty()) return new Response(404, "no route for " + path, null);   // the resource doesn't exist
            for (Route r : fits) {
                if (r.method().equals(method)) return r.handler().apply(match(r.pattern(), path));
            }
            String allow = fits.stream().map(Route::method).distinct().collect(Collectors.joining(", "));
            return new Response(405, method + " is not supported here", allow);           // it exists, wrong verb
        }
    }

    // "/books/{id}" + "/books/42" -> {id=42}; "/books" + "/books" -> {}; no fit -> null
    static Map<String, String> match(String pattern, String path) {
        String[] want = pattern.split("/");
        String[] got = path.split("/");
        if (want.length != got.length) return null;              // /books/42/extra is not /books/{id}
        Map<String, String> params = new LinkedHashMap<>();
        for (int i = 0; i < want.length; i++) {
            String w = want[i], g = got[i];
            if (w.startsWith("{") && w.endsWith("}")) {
                if (g.isEmpty()) return null;
                params.put(w.substring(1, w.length() - 1), g);
            } else if (!w.equals(g)) {
                return null;                                         // literal segments must be equal
            }
        }
        return params;
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
