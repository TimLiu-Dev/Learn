import java.io.IOException;
import java.nio.file.*;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Stream;

public class Links {
    static String describe(Path p) throws IOException {
        return p.getFileName() + ": inode " + Files.getAttribute(p, "unix:ino", LinkOption.NOFOLLOW_LINKS)
            + ", links " + Files.getAttribute(p, "unix:nlink", LinkOption.NOFOLLOW_LINKS)
            + ", symlink " + Files.isSymbolicLink(p);
    }

    public static void main(String[] args) throws IOException {
        Path dir = Files.createTempDirectory("links");
        Path original = Files.writeString(dir.resolve("notes.txt"), "hello\n");
        Path hard = Files.createLink(dir.resolve("hard.txt"), original);          // a second name, same inode
        Path soft = Files.createSymbolicLink(dir.resolve("soft.txt"), Path.of("notes.txt")); // a file holding a path
        for (Path p : List.of(original, hard, soft)) System.out.println(describe(p));
        System.out.println("soft.txt points to: " + Files.readSymbolicLink(soft));

        Files.delete(original);
        System.out.println("after deleting notes.txt:");
        System.out.println("  hard.txt still reads: " + Files.readString(hard).trim());
        System.out.println("  soft.txt exists? " + Files.exists(soft)
            + "  (link itself: " + Files.exists(soft, LinkOption.NOFOLLOW_LINKS) + ")");

        // The deploy trick: build the new link beside the old one, then rename it over the top.
        Files.createDirectory(dir.resolve("v1"));
        Files.createDirectory(dir.resolve("v2"));
        Path current = Files.createSymbolicLink(dir.resolve("current"), Path.of("v1"));
        Path next = Files.createSymbolicLink(dir.resolve("current.tmp"), Path.of("v2"));
        Files.move(next, current, StandardCopyOption.ATOMIC_MOVE);             // one rename(2): never missing
        System.out.println("current -> " + Files.readSymbolicLink(current));
        try (Stream<Path> all = Files.walk(dir)) {
            all.sorted(Comparator.reverseOrder()).forEach(p -> p.toFile().delete());
        }
    }
}
