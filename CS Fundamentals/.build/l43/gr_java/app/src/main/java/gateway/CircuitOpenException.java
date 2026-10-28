package gateway;

public final class CircuitOpenException extends RuntimeException {
    public CircuitOpenException() { super("circuit open"); }
}
