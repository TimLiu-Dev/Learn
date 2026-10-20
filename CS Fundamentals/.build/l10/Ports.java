import java.util.List;
import java.util.OptionalInt;

public class Ports {
    // Design 1: throw. The caller must not pass garbage.
    static int parsePortOrThrow(String text) {
        int n;
        try {
            n = Integer.parseInt(text);
        } catch (NumberFormatException e) {
            throw new IllegalArgumentException("not a number: '" + text + "'", e);
        }
        if (n < 1 || n > 65535) throw new IllegalArgumentException("out of range: " + n);
        return n;
    }

    // Design 2: return an empty Optional. "No port" is a normal outcome.
    static OptionalInt parsePortOrEmpty(String text) {
        try {
            int n = Integer.parseInt(text);
            return (n >= 1 && n <= 65535) ? OptionalInt.of(n) : OptionalInt.empty();
        } catch (NumberFormatException e) {
            return OptionalInt.empty();
        }
    }

    // Design 3: a sealed result. Failure is normal AND the caller needs the reason.
    sealed interface PortResult permits Ok, NotANumber, OutOfRange {}
    record Ok(int port) implements PortResult {}
    record NotANumber(String input) implements PortResult {}
    record OutOfRange(int value) implements PortResult {}

    static PortResult parsePort(String text) {
        int n;
        try {
            n = Integer.parseInt(text);
        } catch (NumberFormatException e) {
            return new NotANumber(text);
        }
        return (n >= 1 && n <= 65535) ? new Ok(n) : new OutOfRange(n);
    }

    public static void main(String[] args) {
        for (String input : List.of("8080", "http", "99999")) {
            String thrown;
            try {
                thrown = String.valueOf(parsePortOrThrow(input));
            } catch (IllegalArgumentException e) {
                thrown = "threw: " + e.getMessage();
            }
            OptionalInt opt = parsePortOrEmpty(input);
            String optional = opt.isPresent() ? String.valueOf(opt.getAsInt()) : "empty";
            String message = switch (parsePort(input)) {
                case Ok ok -> "port " + ok.port();
                case NotANumber nan -> "'" + nan.input() + "' is not a number";
                case OutOfRange oor -> oor.value() + " is outside 1..65535";
            };
            System.out.println(input + " -> " + thrown + " | " + optional + " | " + message);
        }
    }
}
