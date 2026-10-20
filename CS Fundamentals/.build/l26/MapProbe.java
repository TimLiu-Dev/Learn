import java.util.HashMap;
import java.util.Map;

// Not shown in the lesson: checks whether entries are really lost, or only the size counter.
public class MapProbe {
    public static void main(String[] args) throws InterruptedException {
        for (int run = 0; run < 3; run++) {
            Map<Integer, Integer> keys = new HashMap<>();
            Thread[] ws = new Thread[2];
            for (int w = 0; w < 2; w++) {
                int base = w * 100_000;
                ws[w] = new Thread(() -> { for (int i = 0; i < 100_000; i++) keys.put(base + i, i); });
                ws[w].start();
            }
            for (Thread t : ws) t.join();
            int present = 0;
            for (int k = 0; k < 200_000; k++) if (keys.containsKey(k)) present++;
            System.out.println("size=" + keys.size() + " present=" + present);
        }
    }
}
