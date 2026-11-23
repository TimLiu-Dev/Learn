public class PrefixRange {
    public static void main(String[] args) {
        int[] a = {3, -1, 4, 1, 5};
        long[] pre = new long[a.length + 1];
        for (int i = 0; i < a.length; i++) pre[i + 1] = pre[i] + a[i];
        System.out.println(pre[4] - pre[1]);
    }
}
