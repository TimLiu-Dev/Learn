package runner;

/** Thrown for anything the user typed wrong: bad flags, a bad jobs file, a dependency cycle. Exit code 2. */
public class InputError extends Exception {
    public InputError(String message) {
        super(message);
    }
}
