import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Stream;

public class Practice {
    record Endpoint(String method, String path, boolean idempotencyKey) { }

    // RFC 9110 §9.2.2: PUT, DELETE and the safe methods are idempotent.
    // A list of what's allowed means an unknown method is treated as not idempotent.
    private static final Set<String> IDEMPOTENT = Set.of("GET", "HEAD", "OPTIONS", "TRACE", "PUT", "DELETE");

    static boolean isIdempotent(String method) {
        return IDEMPOTENT.contains(method);
    }

    // One review comment per endpoint a client can't safely retry after a timeout.
    static List<String> retryProblems(List<Endpoint> api) {
        return api.stream()
            .filter(e -> !isIdempotent(e.method()) && !e.idempotencyKey())
            .map(e -> e.method() + " " + e.path() + " needs an Idempotency-Key")
            .toList();
    }

    // CHECKS
    public static void main(String[] args) {
        List<Endpoint> shortener = List.of(
            new Endpoint("POST", "/api/links", false),
            new Endpoint("GET", "/{code}", false),
            new Endpoint("GET", "/api/links/{code}/stats", false));
        check("PUT, DELETE and the safe methods are idempotent", true,
            () -> Stream.of("GET", "HEAD", "OPTIONS", "TRACE", "PUT", "DELETE").allMatch(Practice::isIdempotent));
        check("POST is not idempotent", false, () -> isIdempotent("POST"));
        check("PATCH is not idempotent", false, () -> isIdempotent("PATCH"));
        check("the shortener without a key gets one comment",
            List.of("POST /api/links needs an Idempotency-Key"), () -> retryProblems(shortener));
        check("an Idempotency-Key answers the comment", List.of(), () -> retryProblems(List.of(
            new Endpoint("POST", "/api/links", true), shortener.get(1), shortener.get(2))));
        check("comments keep the API's order, and PATCH counts",
            List.of("PATCH /api/links/{code} needs an Idempotency-Key", "POST /api/pastes needs an Idempotency-Key"),
            () -> retryProblems(List.of(
                new Endpoint("PATCH", "/api/links/{code}", false),
                new Endpoint("DELETE", "/api/links/{code}", false),
                new Endpoint("POST", "/api/pastes", false))));
        check("an unknown method is not assumed safe", List.of("PURGE /cache needs an Idempotency-Key"),
            () -> retryProblems(List.of(new Endpoint("PURGE", "/cache", false))));
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
