import java.util.function.ToIntFunction;

public class LogTwice {
    // A stand-in for a real logger: in production each call would also print the whole stack trace.
    static void log(String msg) { System.out.println("ERROR " + msg); }

    static int readPort(String text) {
        if (!text.matches("\\d{1,5}")) throw new IllegalArgumentException("not a port: '" + text + "'");
        return Integer.parseInt(text);
    }

    static int loadNoisy(String text) {
        try {
            return readPort(text);
        } catch (IllegalArgumentException e) {
            log("loading config failed: " + e.getMessage());   // logs...
            throw e;                                            // ...AND rethrows: the caller will log it again
        }
    }

    static int loadQuiet(String text) {
        try {
            return readPort(text);
        } catch (IllegalArgumentException e) {
            throw new IllegalStateException("config 'port' is invalid", e);  // add context, keep the cause, don't log
        }
    }

    static void handleRequest(ToIntFunction<String> load) {
        try {
            load.applyAsInt("80a");
        } catch (Exception e) {                                 // the top level: the ONE place that logs
            log("request failed: " + e.getMessage()
                + (e.getCause() != null ? " (cause: " + e.getCause().getMessage() + ")" : ""));
        }
    }

    public static void main(String[] args) {
        handleRequest(LogTwice::loadNoisy);
        System.out.println("---");
        handleRequest(LogTwice::loadQuiet);
    }
}
