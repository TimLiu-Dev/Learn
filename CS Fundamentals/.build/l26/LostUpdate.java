public class LostUpdate {
    static int count = 0;

    public static void main(String[] args) throws InterruptedException {
        for (int run = 1; run <= 5; run++) {
            count = 0;
            Thread a = new Thread(LostUpdate::work);
            Thread b = new Thread(LostUpdate::work);
            a.start(); b.start();
            a.join(); b.join();
            System.out.println("run " + run + ": count = " + count + " (expected 2000000)");
        }
    }

    static void work() {
        for (int i = 0; i < 1_000_000; i++) count++;
    }
}
