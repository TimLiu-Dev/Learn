import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

// A thumbnail service: requests arrive in batches, thumbnails are cached, and a
// report counts the distinct images per batch. It has a leak and a hotspot.
public class Thumbnails {
    static final Map<String, byte[]> CACHE = new HashMap<>();   // shared by all requests

    static byte[] render(String id) {                          // pretend to resize an image
        byte[] pixels = new byte[1_000];
        pixels[0] = (byte) id.hashCode();
        return pixels;
    }

    static byte[] thumbnail(String id) {
        return CACHE.computeIfAbsent(id, Thumbnails::render);
    }

    static List<String> distinct(List<String> ids) {          // keeps first-seen order
        List<String> out = new ArrayList<>();
        for (String id : ids) {
            if (!out.contains(id)) out.add(id);
        }
        return out;
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
