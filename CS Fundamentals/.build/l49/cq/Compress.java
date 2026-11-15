import java.util.Arrays;

public class Compress {
    static int[] parent = {0, 0, 1, 2, 3};             // a chain: 4 -> 3 -> 2 -> 1 -> 0

    static int find(int p) {                           // recursive path compression
        if (parent[p] != p) parent[p] = find(parent[p]);
        return parent[p];
    }

    public static void main(String[] args) {
        find(3);
        System.out.println(Arrays.toString(parent));
    }
}
