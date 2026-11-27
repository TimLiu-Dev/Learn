import java.nio.ByteBuffer;
import java.nio.charset.StandardCharsets;
import java.security.GeneralSecurityException;
import java.security.KeyFactory;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.MessageDigest;
import java.security.PrivateKey;
import java.security.SecureRandom;
import java.security.Signature;
import java.security.spec.X509EncodedKeySpec;
import java.util.Arrays;
import java.util.Base64;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import tools.jackson.databind.JsonNode;
import tools.jackson.databind.json.JsonMapper;

// A simplified WebAuthn: the real data formats (CBOR, COSE keys, attestation) are left out; the signed bytes are the real shape.
public class Passkey {
    static final JsonMapper json = JsonMapper.builder().build();
    static final Base64.Encoder b64 = Base64.getUrlEncoder().withoutPadding();

    static byte[] sha256(byte[] b) throws GeneralSecurityException { return MessageDigest.getInstance("SHA-256").digest(b); }
    static byte[] utf8(String s) { return s.getBytes(StandardCharsets.UTF_8); }
    static byte[] concat(byte[] a, byte[] b) {
        byte[] r = Arrays.copyOf(a, a.length + b.length);
        System.arraycopy(b, 0, r, a.length, b.length);
        return r;
    }

    record Assertion(String credentialId, byte[] clientDataJSON, byte[] authenticatorData, byte[] signature) {}
    record Registered(String id, byte[] publicKey) {}

    // The authenticator (the phone's password manager, a security key): private keys never leave it.
    static final class Authenticator {
        private final Map<String, PrivateKey> keys = new HashMap<>();   // credential id -> private key (scoped to one RP ID)
        private int signCount = 0;
        Registered register(String rpId) throws GeneralSecurityException {
            KeyPair pair = KeyPairGenerator.getInstance("Ed25519").generateKeyPair();   // COSE alg -8, L33's signature
            byte[] raw = new byte[16];
            new SecureRandom().nextBytes(raw);
            String id = b64.encodeToString(raw);
            keys.put(id, pair.getPrivate());
            return new Registered(id, pair.getPublic().getEncoded());   // the server gets only the public half
        }
        // authenticatorData = SHA-256(RP ID) ‖ flags ‖ signCount; the signature covers it and SHA-256(clientDataJSON).
        Assertion get(String id, String rpId, byte[] clientDataJSON) throws GeneralSecurityException {
            byte[] authData = ByteBuffer.allocate(37).put(sha256(utf8(rpId))).put((byte) 0x05).putInt(++signCount).array();  // flags: UP + UV
            Signature s = Signature.getInstance("Ed25519");
            s.initSign(keys.get(id));
            s.update(concat(authData, sha256(clientDataJSON)));
            return new Assertion(id, clientDataJSON, authData, s.sign());
        }
    }

    // The browser writes clientDataJSON itself, with the origin of the page that asked. The page can't choose it.
    static byte[] clientData(String challenge, String origin) {
        return utf8(json.createObjectNode().put("type", "webauthn.get").put("challenge", challenge).put("origin", origin).toString());
    }

    // The relying party (our server): stores public keys and one-time challenges, never a secret.
    static final class RelyingParty {
        private final String rpId, origin;
        private final Map<String, byte[]> publicKeys = new HashMap<>();
        private final Set<String> challenges = new HashSet<>();
        RelyingParty(String rpId, String origin) { this.rpId = rpId; this.origin = origin; }
        void store(String id, byte[] publicKey) { publicKeys.put(id, publicKey); }
        String challenge() {
            byte[] raw = new byte[32];
            new SecureRandom().nextBytes(raw);
            String c = b64.encodeToString(raw);
            challenges.add(c);
            return c;
        }

        // The checks of WebAuthn §7.2, in its order (simplified)
        String verify(Assertion a) throws GeneralSecurityException {
            JsonNode c = json.readTree(new String(a.clientDataJSON(), StandardCharsets.UTF_8));
            if (!"webauthn.get".equals(c.path("type").asString(null))) return "rejected: wrong type";
            if (!challenges.remove(c.path("challenge").asString(""))) return "rejected: unknown or used challenge";
            if (!origin.equals(c.path("origin").asString(null))) return "rejected: wrong origin " + c.path("origin").asString();
            if (!Arrays.equals(Arrays.copyOf(a.authenticatorData(), 32), sha256(utf8(rpId)))) return "rejected: wrong RP ID";
            if ((a.authenticatorData()[32] & 0x01) == 0) return "rejected: user not present";
            byte[] pk = publicKeys.get(a.credentialId());
            if (pk == null) return "rejected: unknown credential";
            Signature s = Signature.getInstance("Ed25519");
            s.initVerify(KeyFactory.getInstance("Ed25519").generatePublic(new X509EncodedKeySpec(pk)));
            s.update(concat(a.authenticatorData(), sha256(a.clientDataJSON())));
            return s.verify(a.signature()) ? "accepted" : "rejected: bad signature";
        }
    }

    public static void main(String[] args) throws Exception {
        var phone = new Authenticator();
        var server = new RelyingParty("books.example", "https://books.example");
        Registered reg = phone.register("books.example");
        server.store(reg.id(), reg.publicKey());
        System.out.println("registered: the server stores a " + reg.publicKey().length + "-byte public key and no secret");

        String c1 = server.challenge();
        Assertion good = phone.get(reg.id(), "books.example", clientData(c1, "https://books.example"));
        System.out.println("1 login at https://books.example        -> " + server.verify(good));

        // A phishing page fetches a real challenge and asks the user's browser to sign it. A real browser refuses outright,
        // because the passkey is scoped to books.example; this toy one obeys, so we can see the server's own check.
        String c2 = server.challenge();
        Assertion phished = phone.get(reg.id(), "books.example", clientData(c2, "https://books-example.evil"));
        System.out.println("2 signed on https://books-example.evil  -> " + server.verify(phished));

        String c3 = server.challenge();
        Assertion relabelled = phone.get(reg.id(), "books.example", clientData(c3, "https://books-example.evil"));
        Assertion edited = new Assertion(reg.id(), clientData(c3, "https://books.example"), relabelled.authenticatorData(), relabelled.signature());
        System.out.println("3 same, origin edited to the real one   -> " + server.verify(edited));

        System.out.println("4 login 1 replayed                      -> " + server.verify(good));
    }
}
