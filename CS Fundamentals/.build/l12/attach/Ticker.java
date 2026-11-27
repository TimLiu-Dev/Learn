// Stands in for a long-running server: it keeps working while you attach a debugger.
public class Ticker {
    public static void main(String[] args) throws InterruptedException {
        long total = 0;
        for (int tick = 1; tick <= 30; tick++) {        // stops by itself after ~30 s
            total += tick * 10L;
            System.out.println("tick " + tick + ", total " + total);
            Thread.sleep(1000);
        }
    }
}
