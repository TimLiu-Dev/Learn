import java.io.IOException;
import java.nio.file.*;
import java.nio.file.attribute.PosixFilePermission;
import java.util.Set;

// Is a file writable by its group? Two ways: the enum set, and one bit of the mode
public class GroupWrite {
    public static void main(String[] args) throws IOException {
        for (String name : args) {
            Set<PosixFilePermission> perms = Files.getPosixFilePermissions(Path.of(name));
            int mode = Perms.toMode(perms);                    // from Perms.java, compiled together
            System.out.printf("%-10s %s  set says %-5b  mask says %b%n", name, Integer.toOctalString(mode),
                perms.contains(PosixFilePermission.GROUP_WRITE), (mode & 020) != 0);
        }
    }
}
