import java.io.*;
import java.nio.file.*;
import java.security.MessageDigest;
import java.util.Base64;
import java.util.HexFormat;

// What's inside an SSH public key line: "ssh-ed25519 <base64 blob> comment"
public class SshPub {
    // An SSH "string": a 4-byte big-endian length, then that many bytes (RFC 4251)
    static byte[] readString(DataInputStream in) throws IOException {
        byte[] bytes = new byte[in.readInt()];
        in.readFully(bytes);
        return bytes;
    }

    public static void main(String[] args) throws Exception {
        String[] parts = Files.readString(Path.of(args[0])).trim().split(" ", 3);
        byte[] blob = Base64.getDecoder().decode(parts[1]);
        DataInputStream in = new DataInputStream(new ByteArrayInputStream(blob));
        String name = new String(readString(in));
        byte[] key = readString(in);
        System.out.println("line:        " + parts[0] + ", comment '" + parts[2] + "'");
        System.out.println("blob:        " + blob.length + " bytes = 4 + " + name.length() + " + 4 + " + key.length);
        System.out.println("key type:    " + name);
        System.out.println("public key:  " + HexFormat.of().formatHex(key));
        byte[] digest = MessageDigest.getInstance("SHA-256").digest(blob);
        System.out.println("fingerprint: SHA256:" + Base64.getEncoder().withoutPadding().encodeToString(digest));
    }
}
