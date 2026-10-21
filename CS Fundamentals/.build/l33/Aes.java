import java.security.SecureRandom;
import javax.crypto.AEADBadTagException;
import javax.crypto.Cipher;
import javax.crypto.KeyGenerator;
import javax.crypto.SecretKey;
import javax.crypto.spec.GCMParameterSpec;

public class Aes {
    static byte[] encrypt(SecretKey key, byte[] iv, byte[] plain) throws Exception {
        Cipher c = Cipher.getInstance("AES/GCM/NoPadding");
        c.init(Cipher.ENCRYPT_MODE, key, new GCMParameterSpec(128, iv));
        return c.doFinal(plain);
    }

    static byte[] decrypt(SecretKey key, byte[] iv, byte[] sealed) throws Exception {
        Cipher c = Cipher.getInstance("AES/GCM/NoPadding");
        c.init(Cipher.DECRYPT_MODE, key, new GCMParameterSpec(128, iv));
        return c.doFinal(sealed);
    }

    public static void main(String[] args) throws Exception {
        KeyGenerator gen = KeyGenerator.getInstance("AES");
        gen.init(256);
        SecretKey key = gen.generateKey();
        byte[] iv = new byte[12];
        new SecureRandom().nextBytes(iv);  // never reuse with the same key
        byte[] sealed = encrypt(key, iv, "meet at noon".getBytes());
        System.out.println("ciphertext+tag: " + sealed.length + " bytes");
        System.out.println("decrypted: " + new String(decrypt(key, iv, sealed)));

        sealed[0] ^= 1;  // flip one bit
        try {
            decrypt(key, iv, sealed);
        } catch (AEADBadTagException e) {
            System.out.println("tampered: " + e.getClass().getSimpleName() + ": " + e.getMessage());
        }
    }
}
