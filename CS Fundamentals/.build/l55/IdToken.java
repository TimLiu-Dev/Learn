import java.util.*;

// The client's checks on an ID token's claims (OIDC Core §3.1.3.7), after its signature was verified.
// Returns every problem, not just the first, so a log line explains the whole rejection.
public class IdToken {
    static List<String> idTokenProblems(Map<String, Object> claims, String issuer, String clientId, String nonce, long now) {
        var problems = new ArrayList<String>();
        if (!issuer.equals(claims.get("iss"))) problems.add("iss must be exactly " + issuer);
        List<?> aud = switch (claims.get("aud")) {
            case String s -> List.of(s);
            case List<?> l -> l;
            case null, default -> List.of();
        };
        if (!aud.contains(clientId)) problems.add("aud must contain " + clientId);
        if (aud.stream().anyMatch(a -> !clientId.equals(a))) problems.add("aud has audiences we don't trust");
        if (!nonce.equals(claims.get("nonce"))) problems.add("nonce doesn't match the one this login sent");
        if (!(claims.get("exp") instanceof Long exp) || now >= exp) problems.add("expired or no exp");
        if (claims.get("sub") == null) problems.add("no sub");
        return problems;
    }

    static Map<String, Object> with(Map<String, Object> base, String key, Object value) {
        var m = new LinkedHashMap<>(base);
        m.put(key, value);
        return m;
    }

    public static void main(String[] args) {
        var iss = "https://as.example";
        Map<String, Object> good = Map.of("iss", iss, "sub", "ana", "aud", "photo-app", "nonce", "n-123", "exp", 2_000L);
        var cases = new LinkedHashMap<String, Map<String, Object>>();
        cases.put("the real one", good);
        cases.put("for another app", with(good, "aud", "other-app"));
        cases.put("shared with others", with(good, "aud", List.of("photo-app", "ad-network")));
        cases.put("replayed from an old login", with(good, "nonce", "n-999"));
        cases.put("lookalike issuer", with(good, "iss", "https://as.example.evil"));
        cases.put("everything wrong", Map.of("iss", "x", "aud", "y", "exp", 10L));
        cases.forEach((name, claims) -> {
            var problems = idTokenProblems(claims, iss, "photo-app", "n-123", 1_000);
            System.out.printf("%-27s %s%n", name, problems.isEmpty() ? "accept" : "reject: " + String.join("; ", problems));
        });
    }
}
