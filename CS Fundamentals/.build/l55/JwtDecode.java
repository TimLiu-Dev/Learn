import java.nio.charset.StandardCharsets;
import java.time.Duration;
import java.time.Instant;
import java.util.Base64;
import java.util.Set;
import tools.jackson.databind.json.JsonMapper;

// A debugging tool: show what a token SAYS. It checks nothing: decoding is not verifying.
public class JwtDecode {
    static void decode(String token, Instant now) {
        var parts = token.split("\\.", -1);
        if (parts.length != 3) throw new IllegalArgumentException("a JWS has 3 parts, this has " + parts.length);
        var dec = Base64.getUrlDecoder();
        System.out.println("header:  " + new String(dec.decode(parts[0]), StandardCharsets.UTF_8));
        var claims = JsonMapper.builder().build().readTree(new String(dec.decode(parts[1]), StandardCharsets.UTF_8));
        for (var e : claims.properties()) {
            var note = Set.of("exp", "iat", "nbf").contains(e.getKey()) && e.getValue().isNumber()
                    ? "  = " + Instant.ofEpochSecond(e.getValue().asLong()) : "";
            System.out.printf("  %-5s %s%s%n", e.getKey(), e.getValue(), note);
        }
        if (claims.path("exp").isNumber()) {
            var left = Duration.between(now, Instant.ofEpochSecond(claims.path("exp").asLong()));
            System.out.println(left.isNegative() ? "EXPIRED " + -left.toMinutes() + " min ago" : "expires in " + left.toMinutes() + " min");
        }
        System.out.println("signature: " + parts[2].length() + " chars, NOT checked (that needs the key)");
    }

    public static void main(String[] args) {
        var token = "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9"
                + ".eyJpc3MiOiJodHRwczovL2Jvb2tzLmV4YW1wbGUiLCJzdWIiOiJhbmEiLCJhdWQiOiJib29rcy1hcGkiLCJpYXQiOjE3OTAwMDAwMDAsImV4cCI6MTc5MDAwMDkwMH0"
                + ".c2lnbmF0dXJlLWJ5dGVzLWdvLWhlcmU";
        decode(token, Instant.parse("2026-09-21T14:30:00Z"));
    }
}
