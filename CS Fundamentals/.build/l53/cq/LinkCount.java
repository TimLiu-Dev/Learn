import java.nio.file.*;

public class LinkCount {
    public static void main(String[] args) throws Exception {
        Path dir = Files.createTempDirectory("lc");
        Path a = Files.writeString(dir.resolve("a.txt"), "hi");
        Path b = Files.createLink(dir.resolve("b.txt"), a);
        Path c = Files.createLink(dir.resolve("c.txt"), b);
        Files.delete(a);
        System.out.println(Files.getAttribute(b, "unix:nlink") + " " + Files.readString(c));
        Files.delete(b);
        Files.delete(c);
        Files.delete(dir);
    }
}
