import java.io.IOException;

public class Tasks {
    public static void runTwice(Runnable action) {    // Runnable: one abstract method
        action.run();
        action.run();
    }

    public interface Formatter {                       // our own single-method interface
        String format(String s);
    }

    public static String apply(String s, Formatter f) {
        return f.format(s);
    }

    public static String readConfig(String path) throws IOException {   // a checked exception
        throw new IOException("no such file: " + path);
    }
}
