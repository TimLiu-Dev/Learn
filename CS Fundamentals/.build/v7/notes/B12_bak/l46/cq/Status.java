import java.util.ArrayList;
import java.util.List;

public class Status {
    static final List<String> log = new ArrayList<>();
    static int responseCode;

    static void handler(String path) {
        if (path.equals("/boom")) throw new IllegalStateException("boom");
        responseCode = 200;                       // "sending" the response
    }

    // Shaped like Shelf's access-log filter.
    static void accessLog(String path) {
        responseCode = -1;                        // what getResponseCode() says before anything is sent
        try {
            handler(path);
        } finally {
            log.add(path + " " + responseCode);
        }
    }

    public static void main(String[] args) {
        accessLog("/ok");
        try {
            accessLog("/boom");
        } catch (IllegalStateException e) {
            log.add("caught " + e.getMessage());
        }
        System.out.println(String.join(" | ", log));
    }
}
