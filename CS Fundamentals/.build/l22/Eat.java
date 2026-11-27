import java.util.ArrayList;
import java.util.List;

public class Eat {
    public static void main(String[] args) {
        long maxMb = Runtime.getRuntime().maxMemory() / 1024 / 1024;
        List<byte[]> chunks = new ArrayList<>();
        try {
            while (true) chunks.add(new byte[8 * 1024 * 1024]);  // 8 MB at a time, all kept reachable
        } catch (OutOfMemoryError e) {
            int held = chunks.size() * 8;
            chunks.clear();                                      // drop them, so there's room to keep going
            System.out.println("max heap " + maxMb + " MB; caught " + e + " after " + held + " MB");
        }
        System.out.println("still alive, exiting normally");
    }
}
