package runner;

import java.io.PrintStream;
import java.util.function.LongSupplier;

/**
 * The one place that prints. Every line is {@code t=NNNN text}, where the time is taken
 * inside the lock, so lines come out in time order and never interleave.
 */
public final class Events {
    private final PrintStream out;
    private final LongSupplier nanoTime;
    private final long startNanos;
    private boolean stopping;   // guarded by this
    private boolean closed;     // guarded by this: the summary has been printed

    public Events(PrintStream out, LongSupplier nanoTime) {
        this.out = out;
        this.nanoTime = nanoTime;
        this.startNanos = nanoTime.getAsLong();
    }

    public Events(PrintStream out) {
        this(out, System::nanoTime);
    }

    public synchronized void emit(String text) {
        print(text);
    }

    /** Prints {@code start} and returns true, unless shutdown has begun. Atomic, so no start ever follows {@code shutdown}. */
    public synchronized boolean startAttempt(String id, int attempt) {
        if (stopping) return false;
        print("start " + id + " attempt=" + attempt);
        return true;
    }

    /** Prints the last line. After it, {@link #beginShutdown()} does nothing, so {@code summary} is always last. */
    public synchronized void summary(String text) {
        print(text);
        closed = true;
    }

    /** Prints {@code shutdown} the first time it's called; false if shutdown had begun or the run is over. */
    public synchronized boolean beginShutdown() {
        if (stopping || closed) return false;
        stopping = true;
        print("shutdown");
        return true;
    }

    public synchronized boolean isStopping() {
        return stopping;
    }

    private void print(String text) {
        long ms = (nanoTime.getAsLong() - startNanos) / 1_000_000;
        out.println("t=%04d %s".formatted(ms, text));
        out.flush();
    }
}
