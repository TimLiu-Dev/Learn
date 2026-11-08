package books;

import java.nio.charset.StandardCharsets;
import java.security.GeneralSecurityException;
import java.security.MessageDigest;
import java.security.SecureRandom;
import java.util.Base64;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.LongSupplier;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.PBEKeySpec;

// Who you are (passwords, sessions) and the CSRF token that proves a request came from our own page.
public class Auth {
    public static final String COOKIE = "__Host-sid";
    public static final String CLEAR = COOKIE + "=; Secure; HttpOnly; SameSite=Lax; Path=/; Max-Age=0";
    public static String cookie(String id) { return COOKIE + "=" + id + "; Secure; HttpOnly; SameSite=Lax; Path=/; Max-Age=28800"; }

    public static final class Session {
        public final String user, csrf;
        public final boolean admin;
        final long created;
        volatile long lastSeen;
        Session(String user, boolean admin, String csrf, long created) {
            this.user = user; this.admin = admin; this.csrf = csrf; this.created = created; this.lastSeen = created;
        }
    }
    public record LoginResult(String id, Session session) {}

    private record Hash(byte[] salt, byte[] hash) {}
    private final SecureRandom rng = new SecureRandom();
    private final Map<String, Hash> users = new HashMap<>();
    private final Hash dummy;                                  // so unknown names cost the same as wrong passwords
    private final Map<String, Session> sessions = new ConcurrentHashMap<>();
    private final Set<String> admins;
    private final LongSupplier now;
    private final long idleMs = 15 * 60_000L, absoluteMs = 8 * 3_600_000L;

    public Auth(Map<String, String> passwords, Set<String> admins, LongSupplier now) {
        passwords.forEach((name, pw) -> users.put(name, hashOf(pw)));   // name -> password, hashed on the way in
        this.dummy = hashOf("no such user");
        this.admins = admins;
        this.now = now;
    }
    public Auth(Map<String, String> passwords, Set<String> admins) { this(passwords, admins, System::currentTimeMillis); }

    private static byte[] pbkdf2(String pw, byte[] salt) {
        try {
            return SecretKeyFactory.getInstance("PBKDF2WithHmacSHA256")
                    .generateSecret(new PBEKeySpec(pw.toCharArray(), salt, 600_000, 256)).getEncoded();
        } catch (GeneralSecurityException e) { throw new IllegalStateException(e); }
    }
    private Hash hashOf(String pw) {
        var salt = new byte[16];
        rng.nextBytes(salt);
        return new Hash(salt, pbkdf2(pw, salt));
    }
    private String random() {
        var b = new byte[32];
        rng.nextBytes(b);
        return Base64.getUrlEncoder().withoutPadding().encodeToString(b);
    }

    /** A fresh session id on every successful login (no fixation), or null. */
    public LoginResult login(String name, String password) {
        Hash stored = name == null ? null : users.get(name);
        Hash h = stored != null ? stored : dummy;
        boolean matches = MessageDigest.isEqual(pbkdf2(password == null ? "" : password, h.salt()), h.hash());
        if (stored == null || !matches) return null;
        var session = new Session(name, admins.contains(name), random(), now.getAsLong());
        var id = random();
        sessions.put(id, session);
        return new LoginResult(id, session);
    }

    public Session session(String id) {
        var s = id == null ? null : sessions.get(id);
        if (s == null) return null;
        long t = now.getAsLong();
        if (t - s.lastSeen >= idleMs || t - s.created >= absoluteMs) {
            sessions.remove(id);
            return null;
        }
        s.lastSeen = t;
        return s;
    }

    public void logout(String id) { if (id != null) sessions.remove(id); }

    public boolean csrfOk(Session s, String presented) {
        return presented != null && MessageDigest.isEqual(s.csrf.getBytes(StandardCharsets.UTF_8), presented.getBytes(StandardCharsets.UTF_8));
    }
}
