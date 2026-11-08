import java.util.Objects;

public class Practice {
    // extends Exception, so in Java it is checked
    static class ConfigException extends Exception {
        ConfigException(String message, Throwable cause) { super(message, cause); }
    }

    static class Conn implements AutoCloseable {
        final String name;
        private final String line;
        boolean closed = false;

        Conn(String name, String line) { this.name = name; this.line = line; }

        String readLine() {
            if (closed) throw new IllegalStateException("connection " + name + " is closed");
            if (line.equals("DROP")) throw new IllegalStateException("connection " + name + " dropped");
            return line;
        }

        @Override public void close() { closed = true; }
    }

    static int readPort(Conn conn) throws ConfigException {
        try (conn) {                       // closed on every way out
            String text = conn.readLine();
            try {
                return Integer.parseInt(text.trim());
            } catch (NumberFormatException e) {
                // translate to this level's exception, and keep the original as the cause
                throw new ConfigException("bad port in " + conn.name + ": '" + text + "'", e);
            }
        }
    }

// CHECKS
    static String outcome(Check c) {
        try {
            return "returned " + c.get();
        } catch (Exception e) {
            String cause = e.getCause() == null ? "" : " caused by " + e.getCause().getClass().getSimpleName();
            return e.getClass().getSimpleName() + cause;
        }
    }

    public static void main(String[] args) {
        check("a good line gives the port", 8080, () -> readPort(new Conn("app.conf", " 8080 ")));
        check("the connection is closed after success", true, () -> { Conn c = new Conn("app.conf", "8080"); readPort(c); return c.closed; });
        check("a bad number becomes a ConfigException that keeps its cause", "ConfigException caused by NumberFormatException", () -> outcome(() -> readPort(new Conn("app.conf", "80x"))));
        check("the message names the source", true, () -> { try { readPort(new Conn("app.conf", "80x")); return false; } catch (ConfigException e) { return e.getMessage().contains("app.conf"); } });
        check("the connection is closed even when parsing fails", true, () -> { Conn c = new Conn("app.conf", "80x"); outcome(() -> readPort(c)); return c.closed; });
        check("a dropped connection passes through unchanged", "IllegalStateException", () -> outcome(() -> readPort(new Conn("app.conf", "DROP"))));
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
