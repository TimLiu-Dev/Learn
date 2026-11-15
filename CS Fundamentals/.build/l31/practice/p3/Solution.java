import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.Objects;

public class Practice {
    // Does the If-None-Match header say the client already has this version?
    static boolean clientHasIt(String ifNoneMatch, String etag) {
        if (ifNoneMatch == null) return false;
        if (ifNoneMatch.trim().equals("*")) return true;                       // "any version you have"
        String want = etag.startsWith("W/") ? etag.substring(2) : etag;
        return Arrays.stream(ifNoneMatch.split(","))
                .map(String::trim)
                .map(t -> t.startsWith("W/") ? t.substring(2) : t)
                .anyMatch(want::equals);
    }

    // The whole HTTP/1.1 response, as it goes on the wire.
    static String respond(String ifNoneMatch, String etag, String cacheControl, String body) {
        if (clientHasIt(ifNoneMatch, etag)) {
            return "HTTP/1.1 304 Not Modified\r\nETag: " + etag + "\r\nCache-Control: " + cacheControl + "\r\n\r\n";
        }
        int length = body.getBytes(StandardCharsets.UTF_8).length;              // bytes, not characters
        return "HTTP/1.1 200 OK\r\nETag: " + etag + "\r\nCache-Control: " + cacheControl + "\r\n"
                + "Content-Length: " + length + "\r\n\r\n" + body;
    }

    // CHECKS
    static String show(String s) { return s.replace("\r", "\\r").replace("\n", "\\n"); }

    public static void main(String[] args) {
        String tag = "\"v7\"";
        check("no If-None-Match gets the full 200",
              show("HTTP/1.1 200 OK\r\nETag: \"v7\"\r\nCache-Control: max-age=60\r\nContent-Length: 6\r\n\r\nhello\n"),
              () -> show(respond(null, tag, "max-age=60", "hello\n")));
        check("a matching tag gets a 304 with no body",
              show("HTTP/1.1 304 Not Modified\r\nETag: \"v7\"\r\nCache-Control: max-age=60\r\n\r\n"),
              () -> show(respond("\"v7\"", tag, "max-age=60", "hello\n")));
        check("a weak tag matches too", true, () -> clientHasIt("W/\"v7\"", tag));
        check("any tag in a list may match", true, () -> clientHasIt("\"v5\", \"v7\"", tag));
        check("a list without it doesn't", false, () -> clientHasIt("\"v5\",\"v6\"", tag));
        check("a quoted tag is not the bare name", false, () -> clientHasIt("v7", tag));
        check("a star matches whatever version exists", true, () -> clientHasIt("*", tag));
        check("Content-Length counts bytes, not characters", true,
              () -> respond(null, tag, "no-cache", "café\n").contains("Content-Length: 6\r\n"));
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
