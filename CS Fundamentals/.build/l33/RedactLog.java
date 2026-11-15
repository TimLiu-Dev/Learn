import java.util.logging.Formatter;
import java.util.logging.Level;
import java.util.logging.LogRecord;
import java.util.logging.Logger;
import java.util.logging.StreamHandler;
import java.util.regex.Pattern;

public class RedactLog {
    // Masks email addresses in every log message, after the parameters are filled in.
    static class RedactingFormatter extends Formatter {
        private static final Pattern EMAIL =
                Pattern.compile("[A-Za-z0-9._%+-]+@[A-Za-z0-9-]+(\\.[A-Za-z0-9-]+)*\\.[A-Za-z]{2,}");

        @Override public String format(LogRecord r) {
            String message = formatMessage(r);  // "{0}" placeholders are replaced here
            return r.getLevel() + " " + r.getLoggerName() + ": " + EMAIL.matcher(message).replaceAll("[email]") + "\n";
        }
    }

    public static void main(String[] args) {
        StreamHandler handler = new StreamHandler(System.out, new RedactingFormatter());
        Logger log = Logger.getLogger("signup");
        log.setUseParentHandlers(false);        // no default console output
        log.addHandler(handler);
        log.info("new user ana.lima@example.com from the web form");
        log.log(Level.WARNING, "reset for {0} failed: {1}", new Object[] {"ben@example.org", "link expired"});
        log.info("password reset for user 42"); // better still: log an id, not the address
        handler.flush();
    }
}
