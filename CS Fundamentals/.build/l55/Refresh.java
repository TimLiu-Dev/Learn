import java.security.SecureRandom;
import java.util.*;

// Refresh token rotation (RFC 9700 §4.14.2): every refresh returns a NEW refresh token and retires the old one.
// If a retired token ever comes back, two parties hold copies, so the whole family is revoked.
public class Refresh {
    sealed interface Outcome permits Issued, Denied {}
    record Issued(String access, String refresh) implements Outcome {}
    record Denied(String reason) implements Outcome {}

    static final class TokenService {
        private static final class Rt {
            final int family; final String user; boolean used;
            Rt(int family, String user) { this.family = family; this.user = user; }
        }
        private final Map<String, Rt> tokens = new HashMap<>();
        private final Set<Integer> revokedFamilies = new HashSet<>();
        private int nextFamily = 1;
        private final SecureRandom rng = new SecureRandom();

        private String random() {
            var b = new byte[32];
            rng.nextBytes(b);
            return Base64.getUrlEncoder().withoutPadding().encodeToString(b);
        }

        synchronized String login(String user) {
            var t = random();
            tokens.put(t, new Rt(nextFamily++, user));
            return t;
        }

        synchronized Outcome refresh(String presented) {
            var rt = tokens.get(presented);
            if (rt == null) return new Denied("unknown token");
            if (revokedFamilies.contains(rt.family)) return new Denied("family revoked: log in again");
            if (rt.used) {
                revokedFamilies.add(rt.family);                // reuse detected: nobody keeps this login
                return new Denied("REUSE of a retired token: family " + rt.family + " revoked");
            }
            rt.used = true;
            var next = random();
            tokens.put(next, new Rt(rt.family, rt.user));
            return new Issued("access(" + rt.user + ", 15 min)", next);
        }
    }

    static void show(String who, Outcome o) {
        var text = switch (o) {
            case Issued(var access, var refresh) -> "ok, " + access + ", new refresh " + refresh.substring(0, 6) + "…";
            case Denied(var reason) -> reason;
        };
        System.out.printf("%-34s -> %s%n", who, text);
    }

    public static void main(String[] args) {
        var auth = new TokenService();
        var rt1 = auth.login("ana");
        var r2 = auth.refresh(rt1);
        show("app refreshes with rt1", r2);
        var rt2 = ((Issued) r2).refresh();
        show("a stolen copy of rt1 is used", auth.refresh(rt1));
        show("app refreshes with its rt2", auth.refresh(rt2));
        show("app sends a made-up token", auth.refresh("AAAA"));
        var fresh = auth.login("ana");
        show("ana logs in again, new family", auth.refresh(fresh));
    }
}
