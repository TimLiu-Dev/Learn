package runner;

/** How a job ended. Every job ends exactly once, with exactly one of these. */
public enum Outcome {
    DONE, GIVEUP, SKIP, CANCEL, DROP;

    public String event() {
        return name().toLowerCase();
    }
}
