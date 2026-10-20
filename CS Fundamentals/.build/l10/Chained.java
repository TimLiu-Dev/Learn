public class Chained {
    static int parsePort(String text) { return Integer.parseInt(text); }

    static int loadConfig(String name) {
        try {
            return parsePort("80x");
        } catch (NumberFormatException e) {
            // translate to an exception that makes sense at THIS level, keeping the cause
            throw new IllegalStateException("could not load config '" + name + "'", e);
        }
    }

    public static void main(String[] args) {
        loadConfig("app.conf");
    }
}
