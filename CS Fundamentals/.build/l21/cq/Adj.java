import java.util.*;

public class Adj {
    public static void main(String[] args) {
        List<List<Integer>> adj = new ArrayList<>(Collections.nCopies(3, new ArrayList<>()));
        adj.get(0).add(1);      // edge 0-1, stored in both lists
        adj.get(1).add(0);
        System.out.println(adj);
    }
}
