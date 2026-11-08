import java.util.EnumSet;

public class Flags {
    static final int READ = 4;    // 100
    static final int WRITE = 2;   // 010
    static final int EXEC = 1;    // 001

    static String rwx(int f) {
        return ((f & READ) != 0 ? "r" : "-") + ((f & WRITE) != 0 ? "w" : "-") + ((f & EXEC) != 0 ? "x" : "-");
    }

    enum Perm { READ, WRITE, EXEC }

    public static void main(String[] args) {
        int f = READ | EXEC;                 // set two flags
        System.out.println("start      " + rwx(f));
        f |= WRITE;                          // set
        System.out.println("add write  " + rwx(f));
        f &= ~EXEC;                          // clear
        System.out.println("drop exec  " + rwx(f));
        f ^= READ;                           // toggle
        System.out.println("flip read  " + rwx(f));
        System.out.println("can write? " + ((f & WRITE) != 0));

        // Unix permissions are three of these, written in octal: 7 = rwx, 5 = r-x
        int mode = 0755;                     // octal literal
        System.out.println("755 = " + mode + " = " + Integer.toBinaryString(mode) + " -> " + rwx(mode >> 6) + rwx((mode >> 3) & 7) + rwx(mode & 7));

        // The typesafe version: an EnumSet is a bit vector underneath
        EnumSet<Perm> perms = EnumSet.of(Perm.READ, Perm.EXEC);
        perms.add(Perm.WRITE);
        perms.remove(Perm.EXEC);
        System.out.println("EnumSet: " + perms + "   contains WRITE? " + perms.contains(Perm.WRITE));
    }
}
