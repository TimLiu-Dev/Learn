import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.function.Predicate;

public class Practice {
    record Request(String client, String path) { }
    record Response(int status, Map<String, String> headers) {
        Response(int status) { this(status, Map.of()); }
    }

    interface Handler { Response handle(Request req); }

    // Like HttpServer's Filter: do something, then call next (or don't), then maybe do more.
    interface Filter { Response filter(Request req, Handler next); }

    // Filters run in the order given: the first one wraps all the others, and the handler.
    // Build from the inside out: the last filter wraps the handler, the first one wraps everything.
    static Handler chain(List<Filter> filters, Handler handler) {
        Handler h = handler;
        for (int i = filters.size() - 1; i >= 0; i--) {
            Filter f = filters.get(i);
            Handler next = h;
            h = req -> f.filter(req, next);
        }
        return h;
    }

    // Answers 429 with Retry-After: 1 when allow says no, without calling next.
    static class RateLimit implements Filter {
        private final Predicate<String> allow;
        RateLimit(Predicate<String> allow) { this.allow = allow; }

        @Override public Response filter(Request req, Handler next) {
            if (allow.test(req.client())) return next.handle(req);
            return new Response(429, Map.of("Retry-After", "1"));   // the chain stops here
        }
    }

    // Records one line per request it sees, once the rest of the chain has answered.
    static class AccessLog implements Filter {
        final List<String> lines;
        AccessLog(List<String> lines) { this.lines = lines; }

        @Override public Response filter(Request req, Handler next) {
            Response res = next.handle(req);
            lines.add(req.client() + " " + req.path() + " " + res.status());
            return res;
        }
    }

    // CHECKS
    static class Trace implements Filter {
        private final String name;
        private final List<String> events;
        Trace(String name, List<String> events) { this.name = name; this.events = events; }

        @Override public Response filter(Request req, Handler next) {
            events.add(name + " in");
            Response res = next.handle(req);
            events.add(name + " out");
            return res;
        }
    }

    public static void main(String[] args) {
        Handler ok = req -> new Response(200);
        check("with no filters the handler answers", 200, () -> chain(List.of(), ok).handle(new Request("ann", "/books")).status());
        check("filters run in the order given and unwind in reverse", List.of("A in", "B in", "handler", "B out", "A out"), () -> {
            List<String> events = new ArrayList<>();
            Handler h = chain(List.of(new Trace("A", events), new Trace("B", events)), req -> { events.add("handler"); return new Response(200); });
            h.handle(new Request("ann", "/books"));
            return events;
        });
        check("a refused request never reaches the handler", "429 1 calls=0", () -> {
            int[] calls = {0};
            Handler h = chain(List.of(new RateLimit(c -> !c.equals("mallory"))), req -> { calls[0]++; return new Response(200); });
            Response res = h.handle(new Request("mallory", "/books"));
            return res.status() + " " + res.headers().get("Retry-After") + " calls=" + calls[0];
        });
        check("an allowed request passes through", 200, () -> chain(List.of(new RateLimit(c -> true)), ok).handle(new Request("ann", "/books")).status());
        check("the access log goes first, so it sees the 429s", List.of("ann /books 200", "mallory /books 429"), () -> {
            List<String> lines = new ArrayList<>();
            Handler h = chain(List.of(new AccessLog(lines), new RateLimit(c -> !c.equals("mallory"))), ok);
            h.handle(new Request("ann", "/books"));
            h.handle(new Request("mallory", "/books"));
            return lines;
        });
        check("put the log after the limiter and the 429s vanish from it", List.of("ann /books 200"), () -> {
            List<String> lines = new ArrayList<>();
            Handler h = chain(List.of(new RateLimit(c -> !c.equals("mallory")), new AccessLog(lines)), ok);
            h.handle(new Request("ann", "/books"));
            h.handle(new Request("mallory", "/books"));
            return lines;
        });
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
