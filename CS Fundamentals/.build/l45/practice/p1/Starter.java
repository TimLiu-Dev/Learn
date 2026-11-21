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
        throw new UnsupportedOperationException("TODO: compare the segments pairwise");
    }

    static Match match(List<Route> routes, String method, String path) {
        throw new UnsupportedOperationException("TODO: bind every route first, then look at the methods");
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
