package runner;

import java.util.List;
import java.util.Map;

/**
 * Runs a plan of jobs and returns how many ended with each {@link Outcome}.
 * Milestone 1: one job at a time. Milestone 2: N at once, a bounded queue between a producer
 * and the workers. Milestone 3: retries, backoff, timeouts. Milestone 4: dependencies.
 * Milestone 5: a clean stop on SIGTERM.
 */
public final class Runner {
    private final List<Job> plan;
    private final Options opts;
    private final Events events;

    public Runner(List<Job> plan, Options opts, Events events) {
        this.plan = plan;
        this.opts = opts;
        this.events = events;
    }

    public Map<Outcome, Integer> run() throws InterruptedException {
        throw new UnsupportedOperationException("TODO: Milestone 1: run each job (sleep for its duration) and print its events");
    }
}
