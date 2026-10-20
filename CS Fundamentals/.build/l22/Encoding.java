import java.io.IOException;
import java.nio.charset.*;
import java.nio.file.*;

public class Encoding {
    public static void main(String[] args) throws IOException {
        String text = "café 😀";
        Path file = Files.createTempFile("l22", ".txt");
        Files.writeString(file, text);                // no charset given: UTF-8 (JEP 400)
        byte[] bytes = Files.readAllBytes(file);

        System.out.println("length: " + text.length() + " chars, " + text.codePointCount(0, text.length())
                + " code points, " + bytes.length + " bytes");
        StringBuilder hex = new StringBuilder();
        for (byte b : bytes) hex.append(String.format("%02x ", b));
        System.out.println(hex.toString().trim());
        System.out.println("decoded as UTF-8:        " + new String(bytes, StandardCharsets.UTF_8));
        System.out.println("decoded as windows-1252: " + new String(bytes, Charset.forName("windows-1252")));
        System.out.println("default charset: " + Charset.defaultCharset());
        Files.delete(file);
    }
}
