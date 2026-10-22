import java.util.ArrayDeque;
import java.util.Deque;

// Exercise 3 solution: retry once on a 5xx, proved with a scripted fake. No network, no mocking library.
public class Ex3 {
    @FunctionalInterface interface HttpPoster { int post(String url, String body); }

    static final class FeeNotifier {
        private final HttpPoster http;
        private final String baseUrl;
        FeeNotifier(HttpPoster http, String baseUrl) { this.http = http; this.baseUrl = baseUrl; }

        boolean lateFee(String member, int cents) {
            for (int attempt = 0; attempt < 2; attempt++) {          // first try + one retry
                int status = http.post(baseUrl + "/payments", "member=" + member + "&cents=" + cents);
                if (status >= 200 && status <= 299) return true;
                if (status < 500 || status > 599) return false;      // 4xx: retrying won't help
            }
            return false;
        }
    }

    // Stub + spy in one: answers from a script, records every call.
    static final class ScriptedPoster implements HttpPoster {
        private final Deque<Integer> script = new ArrayDeque<>();
        int calls = 0;
        ScriptedPoster(int... statuses) { for (int s : statuses) script.add(s); }
        public int post(String url, String body) { calls++; return script.removeFirst(); }
    }

    static void check(String name, boolean ok) { System.out.println((ok ? "PASS" : "FAIL") + "  " + name); }

    public static void main(String[] args) {
        ScriptedPoster p1 = new ScriptedPoster(201);
        check("success first time: 1 call", new FeeNotifier(p1, "x").lateFee("ada", 75) && p1.calls == 1);
        ScriptedPoster p2 = new ScriptedPoster(503, 201);
        check("503 then 201: true after 2 calls", new FeeNotifier(p2, "x").lateFee("ada", 75) && p2.calls == 2);
        ScriptedPoster p3 = new ScriptedPoster(503, 503);
        check("503 twice: false after 2 calls", !new FeeNotifier(p3, "x").lateFee("ada", 75) && p3.calls == 2);
        ScriptedPoster p4 = new ScriptedPoster(400);
        check("400: false, no retry", !new FeeNotifier(p4, "x").lateFee("ada", 75) && p4.calls == 1);
    }
}
