import java.util.*;

public class Practice {
    sealed interface Match permits Found, WrongMethod, NoRoute {}
    record Found(String name, Map<String, String> params) implements Match {}
    record WrongMethod(List<String> allowed) implements Match {}
    record NoRoute() implements Match {}

    record Route(String method, String template, String name) {}

    static List<String> segments(String path) {
        return Arrays.stream(path.split("/")).filter(s -> !s.isEmpty()).toList();
    }

    // "/books/{id}" against "/books/7" gives {id=7}; null when the shapes differ.
    static Map<String, String> bind(String template, String path) {
        List<String> t = segments(template), p = segments(path);
        if (t.size() != p.size()) return null;
        Map<String, String> params = new HashMap<>();
        for (int i = 0; i < t.size(); i++) {
            String ts = t.get(i);
            if (ts.startsWith("{") && ts.endsWith("}")) params.put(ts.substring(1, ts.length() - 1), p.get(i));
            else if (!ts.equals(p.get(i))) return null;
        }
        return params;
    }

    static Match match(List<Route> routes, String method, String path) {
        // Match the path first, for every method: that's what separates 405 from 404.
        List<String> methods = new ArrayList<>();
        for (Route r : routes) {
            Map<String, String> params = bind(r.template(), path);
            if (params == null) continue;
            if (r.method().equals(method)) return new Found(r.name(), params);
            methods.add(r.method());
        }
        if (methods.isEmpty()) return new NoRoute();
        return new WrongMethod(methods.stream().distinct().sorted().toList());
    }

    // CHECKS
    static final List<Route> ROUTES = List.of(
        new Route("GET", "/books", "list"),
        new Route("POST", "/books", "create"),
        new Route("GET", "/books/{id}", "show"),
        new Route("DELETE", "/books/{id}", "remove"),
        new Route("POST", "/loans/{id}/return", "giveBack"),
        new Route("GET", "/members/{member}/loans/{loan}", "memberLoan"));

    public static void main(String[] args) {
        check("a template binds its path parameters", new Found("giveBack", Map.of("id", "42")), () -> match(ROUTES, "POST", "/loans/42/return"));
        check("two parameters bind by name", new Found("memberLoan", Map.of("member", "7", "loan", "3")), () -> match(ROUTES, "GET", "/members/7/loans/3"));
        check("empty segments do not matter", new Found("show", Map.of("id", "5")), () -> match(ROUTES, "GET", "//books//5"));
        check("PATCH /books/7 lists the allowed methods, sorted", new WrongMethod(List.of("DELETE", "GET")), () -> match(ROUTES, "PATCH", "/books/7"));
        check("allowed methods are listed once each", new WrongMethod(List.of("GET")),
            () -> match(List.of(new Route("GET", "/books/{id}", "byId"), new Route("GET", "/books/{isbn}", "byIsbn")), "DELETE", "/books/7"));
        check("an unknown path is no route at all", new NoRoute(), () -> match(ROUTES, "GET", "/books/7/pages"));
        check("a different literal segment is no route", new NoRoute(), () -> match(ROUTES, "POST", "/loans/42/renew"));
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
