import java.util.*;
import java.util.function.BiFunction;
import java.util.stream.Collectors;

public class Practice {
    record LoanIn(Long bookId, Long memberId) {}
    record LoanOut(long id, long bookId, long memberId, String loaned, String returned) {}
    record Response(int status, String body, String type, Map<String, String> headers) {}

    sealed interface LendResult permits Lent, UnknownBook, UnknownMember, BookOnLoan, LimitReached {}
    record Lent(LoanOut loan) implements LendResult {}
    record UnknownBook() implements LendResult {}
    record UnknownMember() implements LendResult {}
    record BookOnLoan() implements LendResult {}
    record LimitReached() implements LendResult {}

    // A tiny JSON writer for this exercise (Shelf uses Jackson): strings, numbers, lists, maps in insertion order, null.
    static String json(Object v) {
        if (v == null) return "null";
        if (v instanceof String s) return "\"" + s.replace("\\", "\\\\").replace("\"", "\\\"") + "\"";
        if (v instanceof Number n) return n.toString();
        if (v instanceof List<?> l) return l.stream().map(Practice::json).collect(Collectors.joining(",", "[", "]"));
        if (v instanceof Map<?, ?> m) return m.entrySet().stream()
            .map(e -> json(e.getKey().toString()) + ":" + json(e.getValue())).collect(Collectors.joining(",", "{", "}"));
        throw new IllegalArgumentException("can't write " + v);
    }

    // RFC 9457: type about:blank, the status phrase as title, media type application/problem+json; no null fields.
    static Response problem(int status, String detail, List<String> errors) {
        throw new UnsupportedOperationException("TODO: build a LinkedHashMap and write it with json(...)");
    }

    static Response postLoan(LoanIn body, BiFunction<Long, Long, LendResult> lend) {
        throw new UnsupportedOperationException("TODO: validate, then map every LendResult with a pattern switch");
    }

    // CHECKS
    static final BiFunction<Long, Long, LendResult> NEVER = (b, m) -> { throw new AssertionError("lend must not be called for an invalid body"); };

    static Response respond(LendResult result) {
        return postLoan(new LoanIn(1L, 2L), (b, m) -> result);
    }

    public static void main(String[] args) {
        Response created = postLoan(new LoanIn(1L, 2L), (b, m) -> new Lent(new LoanOut(5, b, m, "2026-10-28", null)));
        check("a loan is created with a Location", Map.of("Location", "/loans/5"), () -> created.headers());
        check("the 201 body has no null fields", "{\"id\":5,\"bookId\":1,\"memberId\":2,\"loaned\":\"2026-10-28\"}", () -> created.body());
        check("missing ids are all reported, before lending",
            "{\"type\":\"about:blank\",\"title\":\"Bad Request\",\"status\":400,\"detail\":\"2 problem(s) with the request\",\"errors\":[\"bookId is required\",\"memberId is required\"]}",
            () -> postLoan(new LoanIn(null, null), NEVER).body());
        check("a problem has its own media type", "application/problem+json", () -> postLoan(new LoanIn(1L, null), NEVER).type());
        check("an unknown book is a 404 without errors",
            "{\"type\":\"about:blank\",\"title\":\"Not Found\",\"status\":404,\"detail\":\"No book with id 1\"}", () -> respond(new UnknownBook()).body());
        check("an unknown member is a 404", 404, () -> respond(new UnknownMember()).status());
        check("a lent book is a 409 Conflict",
            "{\"type\":\"about:blank\",\"title\":\"Conflict\",\"status\":409,\"detail\":\"Book 1 is already on loan\"}", () -> respond(new BookOnLoan()).body());
        check("a full card is a 409 Conflict",
            "{\"type\":\"about:blank\",\"title\":\"Conflict\",\"status\":409,\"detail\":\"Member 2 already has 3 open loans\"}", () -> respond(new LimitReached()).body());
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
