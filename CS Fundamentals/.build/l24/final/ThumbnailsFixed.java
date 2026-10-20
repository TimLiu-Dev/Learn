import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Random;

// Fixed: a bounded LRU cache (no leak) and a hash-based distinct (no hotspot).
public class ThumbnailsFixed {
    static final int MAX_ENTRIES = 2_000;                       // enough for one batch's images
    static final Map<String, byte[]> CACHE =
        new LinkedHashMap<>(16, 0.75f, true) {                  // access order = LRU (L19)
            @Override
            protected boolean removeEldestEntry(Map.Entry<String, byte[]> eldest) {
                return size() > MAX_ENTRIES;
            }
        };

    static byte[] render(String id) {                          // pretend to resize an image
        byte[] pixels = new byte[1_000];
        pixels[0] = (byte) id.hashCode();
        return pixels;
    }

    static byte[] thumbnail(String id) {
        return CACHE.computeIfAbsent(id, ThumbnailsFixed::render);
    }

    static List<String> distinct(List<String> ids) {          // keeps first-seen order
        return new ArrayList<>(new LinkedHashSet<>(ids));      // O(1) contains per id (L19)
    }

    public static void main(String[] args) {
        Random random = new Random(42);
        long start = System.nanoTime();
        long checksum = 0;
        for (int batch = 1; batch <= 1_000; batch++) {
            List<String> requests = new ArrayList<>();
            for (int i = 0; i < 4_000; i++) {                  // new images keep arriving
                requests.add("img" + (batch * 1_000 + random.nextInt(3_000)));
            }
            for (String id : requests) checksum += thumbnail(id)[0];
            int unique = distinct(requests).size();
            if (batch % 100 == 0) {
                System.out.println("batch " + batch + ": " + unique + " distinct, cache " + CACHE.size());
            }
        }
        System.out.println("checksum " + checksum + ", " + (System.nanoTime() - start) / 1_000_000 + " ms");
    }
}
