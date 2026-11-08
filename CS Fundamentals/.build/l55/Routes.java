import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

// Fail closed at startup: every route must say who may call it, or the server refuses to start.
public class Routes {
    enum Access { PUBLIC, LOGGED_IN, OWNER_OR_ADMIN, ADMIN }

    record Route(String method, String path, Access access) {          // null access = somebody forgot
        Route withAccess(Access a) { return new Route(method, path, a); }
    }

    static void checkRoutes(List<Route> routes) {
        var unguarded = routes.stream().filter(r -> r.access() == null)
                .map(r -> r.method() + " " + r.path()).collect(Collectors.joining(", "));
        if (!unguarded.isEmpty()) throw new IllegalStateException("refusing to start: no access rule for " + unguarded);
    }

    public static void main(String[] args) {
        var routes = new ArrayList<>(List.of(
                new Route("GET", "/books", Access.PUBLIC),
                new Route("GET", "/books/{id}", Access.PUBLIC),
                new Route("POST", "/books", Access.LOGGED_IN),
                new Route("PUT", "/books/{id}", Access.OWNER_OR_ADMIN)));
        routes.add(new Route("DELETE", "/books/{id}", null));          // added in a hurry
        routes.add(new Route("GET", "/admin/users", null));            // and another
        try {
            checkRoutes(routes);
        } catch (IllegalStateException e) {
            System.out.println(e.getMessage());
        }
        routes.replaceAll(r -> r.access() != null ? r
                : r.withAccess(r.path().startsWith("/admin") ? Access.ADMIN : Access.OWNER_OR_ADMIN));
        checkRoutes(routes);
        System.out.println("all " + routes.size() + " routes have a rule: starting");
        for (var r : routes) System.out.printf("  %-6s %-13s %s%n", r.method(), r.path(), r.access());
    }
}
