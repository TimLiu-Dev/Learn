import java.util.ArrayList;
import java.util.List;

// Three instances, a database that is down from t=10 s to t=20 s, and an orchestrator
// that probes every second and restarts an instance after 3 failed liveness checks in a row.
// Starting an instance takes 20 s. Everything is simulated, one step per second.
public class ProbeSim {
    static class Instance {
        final String name;
        int startingUntil = 0;     // not serving before this second
        int liveFailures = 0;
        Instance(String name) { this.name = name; }
    }

    static String simulate(boolean liveChecksDb) {
        List<Instance> instances = List.of(new Instance("a"), new Instance("b"), new Instance("c"));
        int restarts = 0, dark = 0;
        List<String> events = new ArrayList<>();
        for (int t = 0; t < 60; t++) {
            boolean dbUp = t < 10 || t >= 20;
            for (Instance i : instances) {
                if (t < i.startingUntil) continue;                     // still starting: not probed yet
                boolean live = liveChecksDb ? dbUp : true;             // /live
                i.liveFailures = live ? 0 : i.liveFailures + 1;
                if (i.liveFailures == 3) {
                    i.startingUntil = t + 20; i.liveFailures = 0; restarts++;
                    events.add("t=" + t + " restart " + i.name);
                }
            }
            int ready = 0;
            for (Instance i : instances) if (t >= i.startingUntil && dbUp) ready++;   // /ready checks the database
            if (ready == 0) dark++;
        }
        String ev = events.isEmpty() ? "no restarts" : String.join(", ", events);
        return ev + "\n  restarts: " + restarts + ", seconds with no ready instance: " + dark;
    }

    public static void main(String[] args) {
        System.out.println("database down from t=10 to t=20");
        System.out.println("/live checks the database:\n  " + simulate(true));
        System.out.println("/live checks only the process, /ready checks the database:\n  " + simulate(false));
    }
}
