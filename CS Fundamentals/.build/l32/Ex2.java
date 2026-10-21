import java.util.List;
import java.util.Map;
import java.util.Set;

public class Ex2 {
    enum Outcome { CREATED, FOUND, UPDATED, DELETED, NOT_FOUND, INVALID, CONFLICT }

    // RFC 9110 §9.2: GET, HEAD, OPTIONS, TRACE are safe; those plus PUT and DELETE are idempotent
    static boolean isSafe(String method) { return Set.of("GET", "HEAD", "OPTIONS", "TRACE").contains(method); }
    static boolean isIdempotent(String method) { return isSafe(method) || Set.of("PUT", "DELETE").contains(method); }

    static int status(Outcome outcome) {
        return switch (outcome) {
            case CREATED -> 201;
            case FOUND, UPDATED -> 200;
            case DELETED -> 204;
            case NOT_FOUND -> 404;
            case INVALID -> 400;
            case CONFLICT -> 409;
        };
    }

    public static void main(String[] args) {
        List<Map.Entry<String, Outcome>> requests = List.of(
            Map.entry("POST /books", Outcome.CREATED),
            Map.entry("GET /books/7", Outcome.FOUND),
            Map.entry("GET /books/99", Outcome.NOT_FOUND),
            Map.entry("PUT /books/7", Outcome.UPDATED),
            Map.entry("POST /books", Outcome.INVALID),
            Map.entry("POST /loans", Outcome.CONFLICT),
            Map.entry("DELETE /books/7", Outcome.DELETED),
            Map.entry("PATCH /books/7", Outcome.UPDATED));
        for (var r : requests) {
            String method = r.getKey().substring(0, r.getKey().indexOf(' '));
            System.out.printf("%-16s -> %d  safe=%-5s auto-retry=%s%n",
                r.getKey(), status(r.getValue()), isSafe(method), isIdempotent(method));
        }
    }
}
