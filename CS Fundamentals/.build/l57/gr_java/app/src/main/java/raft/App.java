package raft;

import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

public class App {
    static final long SEED = 7;

    // The final's scripted run: commit two writes, split the cluster, write on both sides, heal.
    static Sim scenario(long seed, boolean split, Sim sim) {
        sim.start();
        sim.runUntil(400);
        int first = sim.leader().id;
        sim.propose(first, "x=1");
        sim.runUntil(450);
        sim.propose(first, "x=2");
        sim.runUntil(700);
        if (split) sim.partition(Set.of(first, IntStream.range(0, sim.n).filter(i -> i != first).findFirst().orElseThrow()));
        sim.runUntil(750);
        sim.propose(first, "x=3");                      // the client still believes 'first' is the leader
        sim.runUntil(1_300);
        sim.propose(sim.leader().id, "x=4");
        sim.runUntil(1_800);
        if (split) sim.heal();
        sim.runUntil(2_300);
        return sim;
    }
    static Sim scenario(long seed, boolean split) { return scenario(seed, split, new Sim(seed)); }

    static void printRun(Sim sim) {
        for (Sim.Event e : sim.history) System.out.println(String.format("%6d ms  %s", e.at(), e.text()));
        System.out.println();
        System.out.println(String.format("%-4s %-4s %-9s %-24s %-6s %s", "node", "term", "role", "log (term:command)", "commit", "applied"));
        for (Sim.Node node : sim.nodes) {
            String log = node.log.stream().map(en -> en.term() + ":" + en.command()).collect(Collectors.joining(" "));
            System.out.println(String.format("%-4s %-4d %-9s %-24s %-6d %s", node.name, node.term, node.role,
                log, node.commitIndex, String.join(", ", node.applied)));
        }
        System.out.println("election safety: " + sim.electionSafe() + ", state machine safety: " + sim.stateMachineSafe());
    }

    // One frame per event, for the lesson's widget.
    static String frames(long seed, boolean split) {
        StringBuilder out = new StringBuilder();
        Sim sim = new Sim(seed);
        sim.onEvent = e -> {
            if (!out.isEmpty()) out.append(",\n");
            out.append("{\"t\":").append(e.at()).append(",\"note\":\"").append(e.text().replace("\"", "\\\"")).append("\"");
            if (e.from() != null) out.append(",\"from\":").append(e.from()).append(",\"to\":").append(e.to());
            if (sim.side() != null)
                out.append(",\"side\":[").append(sim.side().stream().sorted().map(String::valueOf).collect(Collectors.joining(","))).append("]");
            out.append(",\"nodes\":[");
            out.append(sim.nodes.stream().map(nd -> "{\"role\":\"" + nd.role + "\",\"term\":" + nd.term + ",\"up\":" + nd.up +
                ",\"commit\":" + nd.commitIndex + ",\"log\":[" +
                nd.log.stream().map(en -> "[" + en.term() + ",\"" + en.command() + "\"]").collect(Collectors.joining(",")) + "]}")
                .collect(Collectors.joining(",")));
            out.append("]}");
        };
        scenario(seed, split, sim);
        return "[\n" + out + "\n]";
    }

    public static void main(String[] args) {
        if (args.length > 0 && args[0].equals("--json")) {
            System.out.println("{\"healthy\":" + frames(SEED, false) + ",\n\"split\":" + frames(SEED, true) + "}");
            return;
        }
        System.out.println("== Seed " + SEED + ": two writes, a partition at 700 ms, a write on each side, heal at 1,800 ms");
        printRun(scenario(SEED, true));
    }
}
