public class Interleave {
    public static void main(String[] args) throws InterruptedException {
        Thread a = new Thread(() -> { for (int i = 0; i < 40; i++) System.out.print("A"); });
        Thread b = new Thread(() -> { for (int i = 0; i < 40; i++) System.out.print("B"); });
        a.start(); b.start();
        a.join(); b.join();
        System.out.println();
    }
}
