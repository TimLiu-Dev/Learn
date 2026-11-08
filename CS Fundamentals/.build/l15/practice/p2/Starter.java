import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.function.Consumer;

public class Practice {
    record Request(String method, String url, Map<String, String> headers) { }

    static class RequestBuilder {
        String method = "GET";
        String url = "";
        private final Map<String, String> headers = new LinkedHashMap<>();

        void header(String name, String value) {
            throw new UnsupportedOperationException("TODO: store it; a repeated name replaces the old value");
        }

        Request build() {
            throw new UnsupportedOperationException("TODO: reject a blank url, then build a Request that cannot change later");
        }
    }

    static Request request(Consumer<RequestBuilder> block) {
        throw new UnsupportedOperationException("TODO: give block a fresh builder, then build()");
    }

    // CHECKS
    public static void main(String[] args) {
        check("the block configures the request", "POST /books {Accept=json}", () -> {
            Request r = request(b -> { b.method = "POST"; b.url = "/books"; b.header("Accept", "json"); });
            return r.method() + " " + r.url() + " " + r.headers();
        });
        check("method defaults to GET", "GET", () -> request(b -> b.url = "/books").method());
        check("a repeated header replaces the value but keeps its place", List.of("A=3", "B=2"), () ->
            request(b -> { b.url = "/a"; b.header("A", "1"); b.header("B", "2"); b.header("A", "3"); })
                .headers().entrySet().stream().map(Object::toString).toList());
        check("a blank url is rejected", "rejected", () -> {
            try { request(b -> b.url = "   "); return "accepted"; }
            catch (IllegalArgumentException e) { return "rejected"; }
        });
        check("a built request doesn't change when the builder is reused", Map.of("A", "1"), () -> {
            RequestBuilder builder = new RequestBuilder();
            builder.url = "/a";
            builder.header("A", "1");
            Request first = builder.build();
            builder.header("B", "2");
            return first.headers();
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
