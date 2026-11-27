import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class Practice {
    record Event(String type, String data, String id) {}

    // Parse a whole text/event-stream body into the events a browser's EventSource would fire.
    static List<Event> parseSse(String stream) {
        throw new UnsupportedOperationException("TODO: go line by line: blank line -> dispatch, ':' -> comment, otherwise field and value");
    }

    // CHECKS
    public static void main(String[] args) {
        check("one event with the default type", List.of(new Event("message", "hello", null)),
              () -> parseSse("data: hello\n\n"));
        check("several data lines join with a newline", List.of(new Event("message", "YHOO\n+2\n10", null)),
              () -> parseSse("data: YHOO\ndata: +2\ndata: 10\n\n"));
        check("comments are ignored and the type resets after each event",
              List.of(new Event("delta", "Hel", null), new Event("message", "lo", null)),
              () -> parseSse(": keep-alive\n\nevent: delta\ndata: Hel\n\ndata: lo\n\n"));
        check("only one leading space is removed", List.of(new Event("message", " two spaces\nnone", null)),
              () -> parseSse("data:  two spaces\ndata:none\n\n"));
        check("the last id carries over to later events",
              List.of(new Event("message", "a", "7"), new Event("message", "b", "7")),
              () -> parseSse("id: 7\ndata: a\n\ndata: b\n\n"));
        check("no data means no event, and an unfinished event is dropped", List.of(),
              () -> parseSse("event: ping\n\ndata: last"));
        check("CRLF line endings work too", List.of(new Event("message", "x", null)),
              () -> parseSse("data: x\r\n\r\n"));
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
