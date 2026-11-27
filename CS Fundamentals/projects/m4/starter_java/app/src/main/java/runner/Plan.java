package runner;

import java.util.List;

/** Orders jobs so every job comes after its dependencies. */
public final class Plan {
    private Plan() {}

    // Milestone 4: a topological order (L21), and an InputError("dependency cycle: a -> c -> b -> a").
    // Until then, file order is fine: milestones 1–3 have no dependencies.
    public static List<Job> order(List<Job> jobs) throws InputError {
        return jobs;
    }
}
