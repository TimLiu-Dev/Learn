import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.Signature;

public class Sign {
    public static void main(String[] args) throws Exception {
        KeyPair pair = KeyPairGenerator.getInstance("Ed25519").generateKeyPair();
        byte[] message = "pay Bob 10".getBytes();

        // Only the private key can sign...
        Signature signer = Signature.getInstance("Ed25519");
        signer.initSign(pair.getPrivate());
        signer.update(message);
        byte[] sig = signer.sign();
        System.out.println("signature: " + sig.length + " bytes");

        // ...but anyone with the public key can verify
        System.out.println("original verifies: " + verify(pair, message, sig));
        System.out.println("altered verifies:  " + verify(pair, "pay Bob 1000".getBytes(), sig));
    }

    static boolean verify(KeyPair pair, byte[] msg, byte[] sig) throws Exception {
        Signature v = Signature.getInstance("Ed25519");
        v.initVerify(pair.getPublic());
        v.update(msg);
        return v.verify(sig);
    }
}
