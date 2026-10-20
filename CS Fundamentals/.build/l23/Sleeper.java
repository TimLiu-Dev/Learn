public class Sleeper {
    public static void main(String[] args) throws InterruptedException {
        int[] big = new int[25_000_000];          // ~100 MB on the heap
        System.out.println("pid " + ProcessHandle.current().pid() + ", holding " + big.length + " ints");
        Thread.sleep(60_000);                     // time to run jcmd from another terminal
    }
}
