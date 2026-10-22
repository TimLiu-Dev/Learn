import java.util.List;

public class Ex1Given {
    static int t(List<Integer> p, boolean m) {
        int s = 0;
        for (int i : p) s += i;       // add up
        if (m) s = s * 90 / 100;      // 10%
        return s;
    }

    public static void main(String[] args) {
        List<Integer> prices = List.of(1299, 850, 2000);
        System.out.println(t(prices, false) + " " + t(prices, true));
    }
}
