import java.nio.ByteBuffer;
import java.nio.charset.StandardCharsets;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.MessageDigest;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.Signature;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Practice {
    static final String ORIGIN = "https://books.example";   // the only page allowed to log in
    static final String RP_ID = "books.example";            // the passkey's scope

    record Assertion(byte[] clientDataJSON, byte[] authenticatorData, byte[] signature) {}

    static byte[] sha256(byte[] b) throws Exception { return MessageDigest.getInstance("SHA-256").digest(b); }

    static byte[] concat(byte[] a, byte[] b) {
        byte[] r = Arrays.copyOf(a, a.length + b.length);
        System.arraycopy(b, 0, r, a.length, b.length);
        return r;
    }

    // Given: reads one string field from flat JSON like {"type":"webauthn.get","challenge":"…","origin":"…"}
    static String field(byte[] json, String name) {
        Matcher m = Pattern.compile("\"" + name + "\"\\s*:\\s*\"([^\"]*)\"").matcher(new String(json, StandardCharsets.UTF_8));
        return m.find() ? m.group(1) : null;
    }

    // null means accept. Order: type, challenge, origin, RP ID hash, user-present flag, signature.
    static String problem(Assertion a, String expectedChallenge, PublicKey key) throws Exception {
        throw new UnsupportedOperationException("TODO: check the client data, then the authenticator data, then the signature");
    }

    // CHECKS
    static final String CH = "c3-Kq9vXwT0";

    static byte[] clientData(String type, String ch, String origin) {
        return ("{\"type\":\"" + type + "\",\"challenge\":\"" + ch + "\",\"origin\":\"" + origin + "\"}").getBytes(StandardCharsets.UTF_8);
    }

    static byte[] authData(String rpId, int flags) throws Exception {
        return ByteBuffer.allocate(37).put(sha256(rpId.getBytes(StandardCharsets.UTF_8))).put((byte) flags).putInt(1).array();
    }

    static Assertion signed(byte[] cd, byte[] ad, PrivateKey key) throws Exception {
        Signature s = Signature.getInstance("Ed25519");
        s.initSign(key);
        s.update(concat(ad, sha256(cd)));
        return new Assertion(cd, ad, s.sign());
    }

    public static void main(String[] args) throws Exception {
        KeyPair pair = KeyPairGenerator.getInstance("Ed25519").generateKeyPair();
        PublicKey pub = pair.getPublic();
        byte[] goodCd = clientData("webauthn.get", CH, ORIGIN), goodAd = authData(RP_ID, 0x05);
        check("a genuine login is accepted", null, () -> problem(signed(goodCd, goodAd, pair.getPrivate()), CH, pub));
        for (String o : List.of("https://books-example.evil", "https://evilbooks.example", "http://books.example", "https://books.example.evil"))
            check("origin " + o, "wrong origin", () -> problem(signed(clientData("webauthn.get", CH, o), goodAd, pair.getPrivate()), CH, pub));
        check("type webauthn.create", "wrong type", () -> problem(signed(clientData("webauthn.create", CH, ORIGIN), goodAd, pair.getPrivate()), CH, pub));
        check("an old challenge", "wrong challenge", () -> problem(signed(clientData("webauthn.get", "old-challenge", ORIGIN), goodAd, pair.getPrivate()), CH, pub));
        check("RP ID evil.example", "wrong RP ID", () -> problem(signed(goodCd, authData("evil.example", 0x05), pair.getPrivate()), CH, pub));
        check("flags 0x00", "user not present", () -> problem(signed(goodCd, authData(RP_ID, 0x00), pair.getPrivate()), CH, pub));
        Assertion onEvil = signed(clientData("webauthn.get", CH, "https://books-example.evil"), goodAd, pair.getPrivate());
        check("origin edited after signing", "bad signature", () -> problem(new Assertion(goodCd, goodAd, onEvil.signature()), CH, pub));
        KeyPair other = KeyPairGenerator.getInstance("Ed25519").generateKeyPair();
        check("signed by another key", "bad signature", () -> problem(signed(goodCd, goodAd, other.getPrivate()), CH, pub));
    }

    interface Check { Object get() throws Exception; }

    static void check(String name, Object expected, Check actual) {
        try {
            Object got = actual.get();
            System.out.println(Objects.equals(expected, got) ? "PASS " + name : "FAIL " + name + ": expected <" + expected + "> but was <" + got + ">");
        } catch (Exception e) {
            System.out.println("FAIL " + name + ": threw " + e);
        }
    }
    // END CHECKS
}
