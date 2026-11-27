package budget;

import java.util.function.Function;

/**
 * The result of parsing one piece of untrusted input: a value, or the reason
 * it was rejected. Bad input is expected here, so it's a value, not an
 * exception (L10).
 */
public sealed interface Parsed<T> {
    record Ok<T>(T value) implements Parsed<T> {}

    record Bad<T>(String reason) implements Parsed<T> {}

    static <T> Parsed<T> ok(T value) {
        return new Ok<>(value);
    }

    static <T> Parsed<T> bad(String reason) {
        return new Bad<>(reason);
    }

    default <R> Parsed<R> map(Function<? super T, ? extends R> transform) {
        return switch (this) {
            case Ok<T>(T value) -> new Ok<>(transform.apply(value));
            case Bad<T>(String reason) -> new Bad<>(reason);
        };
    }
}
