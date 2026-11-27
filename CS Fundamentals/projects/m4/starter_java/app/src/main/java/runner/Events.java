package runner;

import java.io.PrintStream;
import java.util.function.LongSupplier;

/** The one place that prints event lines: {@code t=NNNN text}, milliseconds since the run started. */
public final class Events {
    private final PrintStream out;
    private final LongSupplier nanoTime;

    public Events(PrintStream out, LongSupplier nanoTime) {
        this.out = out;
        this.nanoTime = nanoTime;
    }

    public Events(PrintStream out) {
        this(out, System::nanoTime);
    }

    public void emit(String text) {
        throw new UnsupportedOperationException("TODO: Milestone 1: print one event line; think about two threads printing at once");
    }

    /** The last line of every run. */
    public void summary(String text) {
        throw new UnsupportedOperationException("TODO: Milestone 1 (Milestone 5: nothing may print after it)");
    }
}
