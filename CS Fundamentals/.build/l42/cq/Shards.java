import java.util.Arrays;

public class Shards {
    public static void main(String[] args) {
        int[] byRange = new int[3];
        int[] byHash = new int[3];
        for (int id = 2991; id <= 3000; id++) {      // today's ten new orders
            byRange[(id - 1) / 1000]++;              // ids 1-1000 on shard 0, 1001-2000 on 1, 2001-3000 on 2
            byHash[id % 3]++;
        }
        System.out.println(Arrays.toString(byRange) + " " + Arrays.toString(byHash));
    }
}
