import java.util.ArrayList;
import java.util.List;

public class Hoard {
    public static void main(String[] args) {
        List<byte[]> hoard = new ArrayList<>();
        while (true) {
            hoard.add(new byte[1_000_000]);   // 1 MB each, all still reachable
            if (hoard.size() % 10 == 0) System.out.println("holding " + hoard.size() + " MB");
        }
    }
}
