public class Median {
    public static void main(String[] args) {
        int[] ms = {100, 3, 2, 4, 3};       // five request latencies, in arrival order
        int sum = 0;
        for (int m : ms) sum += m;
        int mean = sum / ms.length;
        int median = ms[ms.length / 2];
        System.out.println(mean + " " + median);
    }
}
