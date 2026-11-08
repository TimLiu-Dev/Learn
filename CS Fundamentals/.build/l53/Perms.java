import java.io.IOException;
import java.nio.file.*;
import java.nio.file.attribute.PosixFilePermission;
import java.nio.file.attribute.PosixFilePermissions;
import java.util.*;
import static java.nio.file.attribute.PosixFilePermission.*;

// Nine permission bits: rwx for owner, group and others. Java has octal literals:
// a leading 0 means base 8, so 0400 is 256 (L48's 010 trap, used on purpose).
public class Perms {
    static final Map<PosixFilePermission, Integer> BIT = new EnumMap<>(Map.of(
        OWNER_READ, 0400, OWNER_WRITE, 0200, OWNER_EXECUTE, 0100,
        GROUP_READ, 040, GROUP_WRITE, 020, GROUP_EXECUTE, 010,
        OTHERS_READ, 04, OTHERS_WRITE, 02, OTHERS_EXECUTE, 01));

    static int toMode(Set<PosixFilePermission> perms) {
        int mode = 0;
        for (PosixFilePermission p : perms) mode |= BIT.get(p);
        return mode;
    }

    static Set<PosixFilePermission> fromMode(int mode) {
        Set<PosixFilePermission> perms = EnumSet.noneOf(PosixFilePermission.class);
        BIT.forEach((p, bit) -> { if ((mode & bit) != 0) perms.add(p); });
        return perms;
    }

    static String show(Path f) throws IOException {
        Set<PosixFilePermission> perms = Files.getPosixFilePermissions(f);
        return PosixFilePermissions.toString(perms) + "  " + Integer.toOctalString(toMode(perms));
    }

    public static void main(String[] args) throws IOException {
        Path f = Path.of("secret.txt");
        Files.deleteIfExists(f);
        Files.createFile(f);                                  // the umask decides these bits
        System.out.println("created:        " + show(f));
        Files.setPosixFilePermissions(f, PosixFilePermissions.fromString("rw-r-----"));
        System.out.println("rw-r-----:      " + show(f));
        Files.setPosixFilePermissions(f, fromMode(0750));
        System.out.println("mode 750:       " + show(f));
        int mode = toMode(Files.getPosixFilePermissions(f));
        System.out.println("owner can run:  " + ((mode & 0100) != 0));
        System.out.println("others can read: " + ((mode & 04) != 0));
    }
}
