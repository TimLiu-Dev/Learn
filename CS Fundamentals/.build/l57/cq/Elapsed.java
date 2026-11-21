public class Elapsed {
    public static void main(String[] args) {
        long start = 1_000_000_000L;   // two System.nanoTime() readings, 3 s apart
        long end = 4_000_000_000L;
        int ms = (int) (end - start) / 1_000_000;
        System.out.println(ms + " ms");
    }
}
