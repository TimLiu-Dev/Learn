public class Cases {
    static int comparisons = 0;

    static int indexOf(int[] a, int target) {
        for (int i = 0; i < a.length; i++) {
            comparisons++;
            if (a[i] == target) return i;
        }
        return -1;
    }

    static int countFor(int[] a, int target) {
        comparisons = 0;
        indexOf(a, target);
        return comparisons;
    }

    public static void main(String[] args) {
        int n = 1000;
        int[] a = new int[n];
        for (int i = 0; i < n; i++) a[i] = i;   // 0, 1, ..., 999
        System.out.println(countFor(a, 0) + " " + countFor(a, 500) + " " + countFor(a, -7));
    }
}
