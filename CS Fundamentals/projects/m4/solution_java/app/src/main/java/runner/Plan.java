package runner;

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;

/** Orders jobs so every job comes after its dependencies (L21's topological sort). */
public final class Plan {
    private Plan() {}

    /**
     * Kahn's algorithm. The ready queue starts with the jobs that have no dependencies,
     * in file order, and stays FIFO, so ties are broken by file order and the result is
     * the same on every run.
     */
    public static List<Job> order(List<Job> jobs) throws InputError {
        Map<String, Job> byId = jobs.stream().collect(Collectors.toMap(Job::id, Function.identity()));
        Map<String, Integer> missing = new HashMap<>();
        Map<String, List<Job>> dependents = new HashMap<>();
        for (Job job : jobs) {
            missing.put(job.id(), Set.copyOf(job.deps()).size());
            dependents.put(job.id(), new ArrayList<>());
        }
        for (Job job : jobs) for (String dep : Set.copyOf(job.deps())) dependents.get(dep).add(job);

        var ready = new ArrayDeque<Job>();
        for (Job job : jobs) if (missing.get(job.id()) == 0) ready.add(job);
        List<Job> order = new ArrayList<>();
        while (!ready.isEmpty()) {
            Job job = ready.removeFirst();
            order.add(job);
            for (Job next : dependents.get(job.id())) {
                if (missing.merge(next.id(), -1, Integer::sum) == 0) ready.addLast(next);
            }
        }
        if (order.size() < jobs.size()) {
            List<Job> stuck = jobs.stream().filter(j -> missing.get(j.id()) > 0).toList();
            throw new InputError("dependency cycle: " + String.join(" -> ", findCycle(stuck, byId)));
        }
        return order;
    }

    /** Every stuck job has a stuck dependency, so walking deps must revisit a job: that's the cycle. */
    private static List<String> findCycle(List<Job> stuck, Map<String, Job> byId) {
        Set<String> stuckIds = stuck.stream().map(Job::id).collect(Collectors.toSet());
        var path = new LinkedHashSet<String>();
        String current = stuck.getFirst().id();
        while (path.add(current)) {
            current = byId.get(current).deps().stream().filter(stuckIds::contains).findFirst().orElseThrow();
        }
        List<String> list = new ArrayList<>(path);
        List<String> cycle = new ArrayList<>(list.subList(list.indexOf(current), list.size()));
        cycle.add(current);
        return cycle;
    }
}
