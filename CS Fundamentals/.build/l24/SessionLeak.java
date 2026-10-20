import java.util.HashMap;
import java.util.Map;

// Every login is remembered forever: the map is static, so it is a GC root.
public class SessionLeak {
    record Session(String user, byte[] state) {}

    static final Map<Integer, Session> SESSIONS = new HashMap<>();

    static void login(int id) {
        SESSIONS.put(id, new Session("user" + id, new byte[100_000])); // ~100 KB each
    }

    public static void main(String[] args) throws InterruptedException {
        for (int id = 0; ; id++) {
            login(id);          // ...and nobody ever calls SESSIONS.remove(id)
            if (id % 100 == 0) System.out.println("sessions: " + SESSIONS.size());
            Thread.sleep(10);   // one login every 10 ms, like a server under load
        }
    }
}
