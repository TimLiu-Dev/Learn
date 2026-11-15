import java.util.HashSet;
import java.util.Set;

public class Lines {
    // How many 128-byte cache lines does a stride touch in an int[1024]?
    static int linesTouched(int step) {
        Set<Integer> lines = new HashSet<>();
        for (int i = 0; i < 1024; i += step) {
            int byteAddress = i * 4;              // an int is 4 bytes
            lines.add(byteAddress / 128);
        }
        return lines.size();
    }

    public static void main(String[] args) {
        System.out.println(linesTouched(16) + " " + linesTouched(32));
    }
}
